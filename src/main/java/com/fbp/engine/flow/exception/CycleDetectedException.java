package com.fbp.engine.flow.exception;

public class CycleDetectedException extends RuntimeException {
    public CycleDetectedException() {
        super("순환 참조가 감지되었습니다.");
    }
}
