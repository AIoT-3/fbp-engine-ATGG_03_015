package com.fbp.engine.protocol.modbus.frame;

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
}
