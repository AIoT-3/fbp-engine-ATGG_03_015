package com.fbp.engine.core.node.exception;

import com.fbp.engine.core.exception.EngineException;

public class NodeNotFoundException extends EngineException {
    public NodeNotFoundException(String id) {
        super("노드를 찾을 수 없습니다: " + id);
    }
}
