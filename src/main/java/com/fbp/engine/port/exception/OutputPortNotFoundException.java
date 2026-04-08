package com.fbp.engine.port.exception;

import com.fbp.engine.exception.EngineException;

public class OutputPortNotFoundException extends EngineException {
    public OutputPortNotFoundException(String nodeId, String portName) {
        super(String.format("Node [%s]의 출력 포트 [%s]를 찾을 수 없습니다.", nodeId, portName));
    }
}
