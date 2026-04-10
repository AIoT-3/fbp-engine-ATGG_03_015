package com.fbp.engine.core.port.exception;

import com.fbp.engine.core.exception.EngineException;

public class InputPortNotFoundException extends EngineException {
    public InputPortNotFoundException(String nodeId, String portName) {
        super(String.format("Node [%s]의 입력 포트 [%s]를 찾을 수 없습니다.", nodeId, portName));
    }
}
