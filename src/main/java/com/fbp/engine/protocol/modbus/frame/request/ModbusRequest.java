package com.fbp.engine.protocol.modbus.frame.request;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;
import com.fbp.engine.protocol.modbus.frame.ModbusMbapHeader;

public record ModbusRequest(
        ModbusMbapHeader header,
        ModbusRequestPdu pdu
) {
    public static ModbusRequest of(
            int transactionId,
            int unitId,
            ModbusRequestPdu pdu
    ) {
        return new ModbusRequest(
                ModbusMbapHeader.requestHeader(transactionId, unitId, pdu),
                pdu
        );
    }

    public ModbusFunctionCode functionCode() {
        return pdu.functionCode();
    }
}
