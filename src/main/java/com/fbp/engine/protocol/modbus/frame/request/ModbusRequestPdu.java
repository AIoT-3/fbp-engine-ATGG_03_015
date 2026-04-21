package com.fbp.engine.protocol.modbus.frame.request;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public sealed interface ModbusRequestPdu
        permits ReadHoldingRegistersRequestPdu,
                WriteSingleRegisterRequestPdu {

    ModbusFunctionCode functionCode();
    int byteLength();
}
