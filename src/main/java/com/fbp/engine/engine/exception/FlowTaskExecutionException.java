package com.fbp.engine.engine.exception;

import com.fbp.engine.exception.EngineException;

public class FlowTaskExecutionException extends EngineException {
    public FlowTaskExecutionException(String flowId, Throwable cause) {
        super("플로우 실행 중 task가 비정상 종료되었습니다: " + flowId, cause);
    }
}
