package com.fbp.engine.core.edge.connection;

public final class ConnectionFactory {
    private ConnectionFactory() {
        /* This utility class should not be instantiated */
    }

    public static Connection create(
            String sourceNodeId,
            String sourcePortName,
            String targetNodeId,
            String targetPortName
    ) {
        return LocalConnection.between(sourceNodeId, sourcePortName, targetNodeId, targetPortName);
    }
}
