package com.fbp.engine.core.flow.exception;

import com.fbp.engine.core.exception.EngineException;

public class CycleDetectedException extends EngineException {
    public CycleDetectedException() {
        super("순환 참조가 감지되었습니다.");
    }
}
