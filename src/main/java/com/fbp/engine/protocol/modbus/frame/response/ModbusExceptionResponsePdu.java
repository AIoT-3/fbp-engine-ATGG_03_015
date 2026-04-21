package com.fbp.engine.protocol.modbus.frame.response;

import com.fbp.engine.protocol.modbus.frame.ModbusFunctionCode;

public record ModbusExceptionResponsePdu(
        ModbusFunctionCode originalFunctionCode,
        int exceptionCode
) implements ModbusResponsePdu {

    private static final int EXCEPTION_FUNCTION_CODE_MASK = 0x80;

    @Override
    public int functionCode() {
        return originalFunctionCode.getCode() | EXCEPTION_FUNCTION_CODE_MASK;
    }

    @Override
    public int byteLength() {
        // functionCode(1) + exceptionCode(1)
        return 2;
    }
}
