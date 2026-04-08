package com.fbp.engine.node.exception;

import com.fbp.engine.exception.EngineException;

public class NodeNotFoundException extends EngineException {
    public NodeNotFoundException(String id) {
        super("노드를 찾을 수 없습니다: " + id);
    }
}
