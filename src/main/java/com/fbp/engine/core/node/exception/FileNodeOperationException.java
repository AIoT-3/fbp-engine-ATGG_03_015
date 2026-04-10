package com.fbp.engine.core.node.exception;

import com.fbp.engine.core.exception.EngineException;

public class FileNodeOperationException extends EngineException {
    public FileNodeOperationException() {
        super("파일 노드에서 문제가 발생했습니다");
    }

    public FileNodeOperationException(String message, Throwable cause) {
        super("파일 노드에서 문제가 발생했습니다: " + message, cause);
    }
}
