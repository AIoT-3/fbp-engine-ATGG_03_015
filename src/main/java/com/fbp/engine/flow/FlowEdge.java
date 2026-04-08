package com.fbp.engine.flow;

import com.fbp.engine.connection.Connection;

public record FlowEdge(
        String sourceNodeId,
        String sourcePortName,
        String targetNodeId,
        String targetPortName,
        Connection connection
) {
}
