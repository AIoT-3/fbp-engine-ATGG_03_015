package com.fbp.engine.app.cli.exception;

import com.fbp.engine.common.exception.FbpException;

import java.io.Serial;

public class CliException extends FbpException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CliException(CliFailureType failureType, Object... args) {
        super(failureType, args);
    }

    public CliException(CliFailureType failureType, Throwable cause, Object... args) {
        super(failureType, cause, args);
    }

    @Override
    public CliFailureType getFailureType() {
        return (CliFailureType) super.getFailureType();
    }
}
