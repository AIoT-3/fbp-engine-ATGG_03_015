package com.fbp.engine.edge;

import com.fbp.engine.edge.connection.Connection;

public record Edge(
        String sourceNodeId,
        String sourcePortName,
        String targetNodeId,
        String targetPortName,
        Connection connection
) {
}
