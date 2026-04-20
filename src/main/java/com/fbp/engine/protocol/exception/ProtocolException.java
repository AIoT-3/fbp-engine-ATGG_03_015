package com.fbp.engine.protocol.exception;

import com.fbp.engine.common.exception.FailureType;
import com.fbp.engine.common.exception.FbpException;

import java.io.Serial;

public abstract class ProtocolException extends FbpException {
    @Serial
    private static final long serialVersionUID = 1L;

    protected ProtocolException(FailureType failureType, Object... args) {
        super(failureType, args);
    }

    protected ProtocolException(FailureType failureType, Throwable cause, Object... args) {
        super(failureType, cause, args);
    }
}
