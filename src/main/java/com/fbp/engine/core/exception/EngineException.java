package com.fbp.engine.core.exception;

import com.fbp.engine.common.exception.FbpException;

import java.io.Serial;

public class EngineException extends FbpException {
    @Serial
    private static final long serialVersionUID = 1L;

    public EngineException(EngineFailureType failureType, Object... args) {
        super(failureType, args);
    }

    public EngineException(EngineFailureType failureType, Throwable cause, Object... args) {
        super(failureType, cause, args);
    }

    @Override
    public EngineFailureType getFailureType() {
        return (EngineFailureType) super.getFailureType();
    }
}
