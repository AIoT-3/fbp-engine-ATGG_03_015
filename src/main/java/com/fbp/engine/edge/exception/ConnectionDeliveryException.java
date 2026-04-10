package com.fbp.engine.edge.exception;

import com.fbp.engine.exception.EngineException;

public class ConnectionDeliveryException extends EngineException {
    public ConnectionDeliveryException(String connectionId, Throwable cause) {
        super("메시지 전달 중 인터럽트가 발생했습니다: " + connectionId, cause);
    }
}
