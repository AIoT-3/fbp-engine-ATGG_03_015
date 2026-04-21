package com.fbp.engine.protocol.modbus.node;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.protocol.ProtocolNode;
import com.fbp.engine.protocol.modbus.client.ModbusTcpClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fbp.engine.protocol.modbus.support.ModbusConfig.*;

public class ModbusReaderNode extends ProtocolNode {
    // ports
    private static final String TRIGGER_PORT = "trigger";
    private static final String OUT_PORT = "out";
    private static final String ERROR_PORT = "error";
    // config keys
    private static final String START_ADDRESS_KEY = "startAddress";
    private static final String COUNT_KEY = "count";
    private static final String REGISTER_MAPPING_KEY = "registerMapping";
    private static final String MAPPING_NAME_KEY = "name";
    private static final String MAPPING_SCALE_KEY = "scale";
    // default values
    private static final int DEFAULT_START_ADDRESS = 0;
    private static final int DEFAULT_COUNT = 1;

    private ModbusTcpClient client;

    public ModbusReaderNode(String id, Map<String, Object> config) {
        super(id, config);
        addInputPort(TRIGGER_PORT);
        addOutputPort(OUT_PORT);
        addOutputPort(ERROR_PORT);
    }

    @Override
    protected void doConnect() {
        client = new ModbusTcpClient(
                getStringConfig(HOST_KEY, DEFAULT_HOST),
                getIntConfig(PORT_KEY, DEFAULT_PORT)
        );
        client.connect();
    }

    @Override
    protected void doDisconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        if (!TRIGGER_PORT.equals(portMessage.inputPortName())) {
            return;
        }

        try {
            int slaveId = getIntConfig(SLAVE_ID_KEY, DEFAULT_SLAVE_ID);
            int startAddress = getIntConfig(START_ADDRESS_KEY, DEFAULT_START_ADDRESS);
            int count = getIntConfig(COUNT_KEY, DEFAULT_COUNT);
            int[] registers = client.readHoldingRegisters(slaveId, startAddress, count);

            send(OUT_PORT, Message.of(toPayload(slaveId, startAddress, registers)));
        } catch (RuntimeException e) {
            sendError(e);
        }
    }

    private Map<String, Object> toPayload(int slaveId, int startAddress, int[] registers) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(SLAVE_ID_KEY, slaveId);
        payload.put(START_ADDRESS_KEY, startAddress);
        payload.put(COUNT_KEY, registers.length);
        payload.put("registers", rawRegisterMap(startAddress, registers));
        payload.put("modbusTimestamp", System.currentTimeMillis());

        applyRegisterMapping(payload, startAddress, registers);

        return payload;
    }

    private Map<String, Integer> rawRegisterMap(int startAddress, int[] registers) {
        Map<String, Integer> rawRegisters = new LinkedHashMap<>();
        for (int i = 0; i < registers.length; i++) {
            rawRegisters.put(String.valueOf(startAddress + i), registers[i]);
        }
        return rawRegisters;
    }

    private void applyRegisterMapping(Map<String, Object> payload, int startAddress, int[] registers) {
        Object mappingValue = getConfigValue(REGISTER_MAPPING_KEY);
        if (!(mappingValue instanceof Map<?, ?> registerMapping)) {
            return;
        }

        for (Map.Entry<?, ?> entry : registerMapping.entrySet()) {
            try {
                int address = Integer.parseInt(String.valueOf(entry.getKey()));
                int index = address - startAddress;
                if (index >= 0 && index < registers.length) {
                    applyMappedValue(payload, entry.getValue(), registers[index]);
                }
            } catch (NumberFormatException ignored) {
                // invalid mapping key is ignored
            }
        }
    }

    private void applyMappedValue(Map<String, Object> payload, Object mappingSpec, int rawValue) {
        if (!(mappingSpec instanceof Map<?, ?> spec)) {
            return;
        }

        Object nameValue = spec.get(MAPPING_NAME_KEY);
        if (!(nameValue instanceof String name) || name.isBlank()) {
            return;
        }

        Object scaleValue = spec.get(MAPPING_SCALE_KEY);
        if (scaleValue instanceof Number scale) {
            payload.put(name, rawValue * scale.doubleValue());
        } else {
            payload.put(name, rawValue);
        }
    }

    private void sendError(RuntimeException e) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nodeId", getId());
        payload.put("error", e.getMessage());

        send(ERROR_PORT, Message.of(payload));
    }
}
