package com.fbp.engine.protocol.modbus.frame.response;

public sealed interface ModbusResponsePdu
        permits ReadHoldingRegistersResponsePdu,
                WriteSingleRegisterResponsePdu,
                ModbusExceptionResponsePdu {
    int functionCode();
    int byteLength();
}
