package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModbusExceptionCode {
    ILLEGAL_FUNCTION(0x01),
    ILLEGAL_DATA_ADDRESS(0x02),
    ILLEGAL_DATA_VALUE(0x03),
    SLAVE_DEVICE_FAILURE(0x04);

    private final int code;

    public static ModbusExceptionCode fromCode(int code) {
        for (ModbusExceptionCode exceptionCode : values()) {
            if (exceptionCode.code == code) {
                return exceptionCode;
            }
        }
        throw new ModbusException(ModbusFailureType.UNKNOWN_EXCEPTION_CODE, code);
    }
}
