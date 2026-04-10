package com.fbp.engine.core.flow.exception;

import com.fbp.engine.core.exception.EngineException;

public class FlowNotFoundException extends EngineException {
    public FlowNotFoundException(String flowId) {
        super("플로우를 찾을 수 없습니다: " + flowId);
    }

    public FlowNotFoundException() {
        super("플로우를 찾을 수 없습니다.");
    }
}
