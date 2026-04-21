package com.fbp.engine.protocol.modbus.frame;

import com.fbp.engine.protocol.modbus.exception.ModbusException;
import com.fbp.engine.protocol.modbus.exception.ModbusFailureType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModbusFunctionCode {
    READ_HOLDING_REGISTERS(0x03),
    READ_INPUT_REGISTERS(0x04),
    WRITE_SINGLE_REGISTER(0x06),
    WRITE_MULTIPLE_REGISTERS(0x10);

    private final int code;

    public static ModbusFunctionCode fromCode(int code) {
        return switch (code) {
            case 0x03 -> READ_HOLDING_REGISTERS;
            case 0x04 -> READ_INPUT_REGISTERS;
            case 0x06 -> WRITE_SINGLE_REGISTER;
            case 0x10 -> WRITE_MULTIPLE_REGISTERS;
            default -> throw new ModbusException(ModbusFailureType.UNKNOWN_FUNCTION_CODE, code);
        };
    }
}
