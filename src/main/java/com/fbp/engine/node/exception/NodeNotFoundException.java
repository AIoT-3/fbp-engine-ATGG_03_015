package com.fbp.engine.node.exception;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException() {
        super("노드를 찾을 수 없습니다.");
    }
}
