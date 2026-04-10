package com.fbp.engine.core.flow.exception;

import com.fbp.engine.core.exception.EngineException;

public class FlowRuntimeException extends EngineException {
    public FlowRuntimeException(String flowId, Throwable cause) {
        super("플로우 실행 중 task가 비정상 종료되었습니다: " + flowId, cause);
    }
}
