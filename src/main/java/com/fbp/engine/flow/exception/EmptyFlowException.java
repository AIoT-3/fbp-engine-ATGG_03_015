package com.fbp.engine.flow.exception;

import com.fbp.engine.exception.EngineException;

public class EmptyFlowException extends EngineException {
    public EmptyFlowException() {
        super("플로우가 가진 노드가 없습니다.");
    }
}
