package com.fbp.engine.core.edge.exception;

import com.fbp.engine.core.exception.EngineException;

public class ConnectionPollException extends EngineException {
    public ConnectionPollException(String connectionId, Throwable cause) {
        super("메시지 폴링 중 인터럽트가 발생했습니다: " + connectionId, cause);
    }
}
