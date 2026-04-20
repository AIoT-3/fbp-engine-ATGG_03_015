package com.fbp.engine.common.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public abstract class FbpException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final FailureType failureType;

    protected FbpException(FailureType failureType, Object... args) {
        super(failureType.formatMessage(args));
        this.failureType = failureType;
    }

    protected FbpException(FailureType failureType, Throwable cause, Object... args) {
        super(failureType.formatMessage(args), cause);
        this.failureType = failureType;
    }
}
