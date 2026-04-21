package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.frame.request.ModbusRequestPdu;
import com.fbp.engine.protocol.modbus.frame.response.ModbusResponsePdu;

public record ModbusMbapHeader(
        int transactionId,
        int protocolId,
        int length,
        int unitId
) {
    public static final int HEADER_LENGTH = 7;
    public static final int PROTOCOL_ID = 0;

    public static ModbusMbapHeader requestHeader(
            int transactionId,
            int unitId,
            ModbusRequestPdu pdu
    ) {
        return new ModbusMbapHeader(
                transactionId,
                PROTOCOL_ID,
                1 + pdu.byteLength(),
                unitId
        );
    }

    public static ModbusMbapHeader responseHeader(
            int transactionId,
            int unitId,
            ModbusResponsePdu pdu
    ) {
        return new ModbusMbapHeader(
                transactionId,
                PROTOCOL_ID,
                1 + pdu.byteLength(),
                unitId
        );
    }
}
