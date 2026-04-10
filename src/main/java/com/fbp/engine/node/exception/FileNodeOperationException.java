package com.fbp.engine.node.exception;

import com.fbp.engine.exception.EngineException;

public class FileNodeOperationException extends EngineException {
    public FileNodeOperationException(String message) {
        super(message);
    }

    public FileNodeOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
