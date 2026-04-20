package com.fbp.engine.protocol.modbus.exception;

import com.fbp.engine.protocol.exception.ProtocolException;

import java.io.Serial;

public class ModbusException extends ProtocolException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ModbusException(ModbusFailureType failureType, Object... args) {
        super(failureType, args);
    }

    public ModbusException(ModbusFailureType failureType, Throwable cause, Object... args) {
        super(failureType, cause, args);
    }

    @Override
    public ModbusFailureType getFailureType() {
        return (ModbusFailureType) super.getFailureType();
    }
}
