package com.fbp.engine.protocol.modbus.frame.request;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public record WriteSingleRegisterRequestPdu(
        int address,
        int value
) implements ModbusRequestPdu {

    @Override
    public ModbusFunctionCode functionCode() {
        return ModbusFunctionCode.WRITE_SINGLE_REGISTER;
    }

    @Override
    public int byteLength() {
        // functionCode(1) + address(2) + value(2)
        return 5;
    }
}
