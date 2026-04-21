package com.fbp.engine.protocol.modbus.node;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.protocol.ProtocolNode;
import com.fbp.engine.protocol.modbus.client.ModbusTcpClient;
import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import com.fbp.engine.protocol.modbus.frame.ModbusValidator;

import java.util.HashMap;
import java.util.Map;

import static com.fbp.engine.protocol.modbus.support.ModbusConfig.*;

public class ModbusWriterNode extends ProtocolNode {
    // ports
    private static final String IN_PORT = "in";
    private static final String RESULT_PORT = "result";
    private static final String ERROR_PORT = "error";
    // config keys
    private static final String REGISTER_ADDRESS_KEY = "registerAddress";
    private static final String VALUE_FIELD_KEY = "valueField";
    private static final String SCALE_KEY = "scale";
    // default values
    private static final int DEFAULT_REGISTER_ADDRESS = 0;
    private static final String DEFAULT_VALUE_FIELD = "value";
    private static final double DEFAULT_SCALE = 1.0;

    private ModbusTcpClient client;

    public ModbusWriterNode(String id, Map<String, Object> config) {
        super(id, config);
        addInputPort(IN_PORT);
        addOutputPort(RESULT_PORT);
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
        if (!IN_PORT.equals(portMessage.inputPortName())) {
            return;
        }

        try {
            int slaveId = getIntConfig(SLAVE_ID_KEY, DEFAULT_SLAVE_ID);
            int registerAddress = getIntConfig(REGISTER_ADDRESS_KEY, DEFAULT_REGISTER_ADDRESS);
            int registerValue = toRegisterValue(portMessage.message());

            client.writeSingleRegister(slaveId, registerAddress, registerValue);
            send(RESULT_PORT, Message.of(resultPayload(slaveId, registerAddress, registerValue)));
        } catch (RuntimeException e) {
            sendError(e);
        }
    }

    private int toRegisterValue(Message message) {
        String valueField = getStringConfig(VALUE_FIELD_KEY, DEFAULT_VALUE_FIELD);
        Object value = message.get(valueField);
        if (!(value instanceof Number number)) {
            throw new ModbusException(ModbusFailureType.MESSAGE_FIELD_INVALID, valueField, value);
        }

        long scaledValue = Math.round(number.doubleValue() * getDoubleConfig(SCALE_KEY, DEFAULT_SCALE));
        if (scaledValue < 0 || scaledValue > 0xFFFF) {
            throw new ModbusException(ModbusFailureType.REGISTER_VALUE_INVALID, scaledValue);
        }

        int registerValue = (int) scaledValue;
        ModbusValidator.validateRegisterValue(registerValue);
        return registerValue;
    }

    private Map<String, Object> resultPayload(int slaveId, int registerAddress, int value) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(SLAVE_ID_KEY, slaveId);
        payload.put(REGISTER_ADDRESS_KEY, registerAddress);
        payload.put(DEFAULT_VALUE_FIELD, value);
        payload.put("modbusTimestamp", System.currentTimeMillis());
        return payload;
    }

    private void sendError(RuntimeException e) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nodeId", getId());
        payload.put("error", e.getMessage());

        send(ERROR_PORT, Message.of(payload));
    }
}
