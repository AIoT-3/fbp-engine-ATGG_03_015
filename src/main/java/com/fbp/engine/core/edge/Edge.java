package com.fbp.engine.core.edge;

import com.fbp.engine.core.edge.connection.Connection;

public record Edge(
        String sourceNodeId,
        String sourcePortName,
        String targetNodeId,
        String targetPortName,
        Connection connection
) {
}
