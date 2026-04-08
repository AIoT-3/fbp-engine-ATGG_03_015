package com.fbp.engine.port.exception;

public class PortNotFoundException extends RuntimeException {
    public PortNotFoundException() {
        super("포트를 찾을 수 없습니다.");
    }
}
