package com.fbp.engine.core.flow.exception;

import com.fbp.engine.core.exception.EngineException;

public class EmptyFlowException extends EngineException {
    public EmptyFlowException() {
        super("플로우가 가진 노드가 없습니다.");
    }
}
