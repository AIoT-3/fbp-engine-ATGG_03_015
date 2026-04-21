package com.fbp.engine.protocol.modbus.frame.response;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public record WriteSingleRegisterResponsePdu(
        int address,
        int value
) implements ModbusResponsePdu {

    @Override
    public int functionCode() {
        return ModbusFunctionCode.WRITE_SINGLE_REGISTER.getCode();
    }

    @Override
    public int byteLength() {
        // functionCode(1) + address(2) + value(2)
        return 5;
    }
}
