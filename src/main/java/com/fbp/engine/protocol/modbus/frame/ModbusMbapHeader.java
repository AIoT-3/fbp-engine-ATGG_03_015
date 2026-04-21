package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.frame.request.ModbusRequestPdu;

public record ModbusMbapHeader(
        int transactionId,
        int protocolId,
        int length,
        int unitId
) {
    private static final int PROTOCOL_ID = 0;

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
}
