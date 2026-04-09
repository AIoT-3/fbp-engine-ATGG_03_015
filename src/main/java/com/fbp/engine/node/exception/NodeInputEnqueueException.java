package com.fbp.engine.node.exception;

import com.fbp.engine.exception.EngineException;

public class NodeInputEnqueueException extends EngineException {
    public NodeInputEnqueueException(String nodeId) {
        super("노드 입력 큐 적재 중 인터럽트가 발생했습니다: " + nodeId);
    }
}
