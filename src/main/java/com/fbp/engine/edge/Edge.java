package com.fbp.engine.edge;

public record Edge(
        String sourceNodeId,
        String sourcePortName,
        String targetNodeId,
        String targetPortName,
        Connection connection
) {
}
