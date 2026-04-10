package com.fbp.engine.edge.exception;

import com.fbp.engine.exception.EngineException;

public class ConnectionPollException extends EngineException {
    public ConnectionPollException(String connectionId, Throwable cause) {
        super("메시지 폴링 중 인터럽트가 발생했습니다: " + connectionId, cause);
    }
}
