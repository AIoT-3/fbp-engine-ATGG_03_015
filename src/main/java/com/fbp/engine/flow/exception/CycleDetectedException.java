package com.fbp.engine.flow.exception;

import com.fbp.engine.exception.EngineException;

public class CycleDetectedException extends EngineException {
    public CycleDetectedException() {
        super("순환 참조가 감지되었습니다.");
    }
}
