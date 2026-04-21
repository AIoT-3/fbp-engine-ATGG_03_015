package com.fbp.engine.protocol.modbus.frame.response;

import com.fbp.engine.protocol.modbus.frame.ModbusMbapHeader;

public record ModbusResponse(
        ModbusMbapHeader header,
        ModbusResponsePdu pdu
) {
}
