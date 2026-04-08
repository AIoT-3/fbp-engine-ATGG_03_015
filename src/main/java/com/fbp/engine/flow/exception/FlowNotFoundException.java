package com.fbp.engine.flow.exception;

import com.fbp.engine.exception.EngineException;

public class FlowNotFoundException extends EngineException {
    public FlowNotFoundException() {
        super("플로우가 없습니다.");
    }
}
