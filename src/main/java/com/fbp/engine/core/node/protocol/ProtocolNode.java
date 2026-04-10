package com.fbp.engine.core.node.protocol;

import com.fbp.engine.core.node.model.AbstractNode;
import lombok.Getter;

import java.util.Map;

public abstract class ProtocolNode extends AbstractNode {
    private static final long DEFAULT_RETRY_INTERVAL_MS = 5000;
    private static final String RETRY_COUNT_KEY = "retryCount";

    private final Map<String, Object> config;
    private final long retryIntervalMs;
    @Getter
    private ProtocolConnectionState connectionState = ProtocolConnectionState.DISCONNECTED;

    protected ProtocolNode(String id, Map<String, Object> config, long retryIntervalMs) {
        super(id);
        this.config = config;
        this.retryIntervalMs = retryIntervalMs;
    }

    protected ProtocolNode(String id, Map<String, Object> config) {
        this(id, config, DEFAULT_RETRY_INTERVAL_MS);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    public abstract void connect();
    public abstract void disconnect();

    public void reconnect() {

    }

    public Object getConfig(String key) {
        return config.get(key);
    }

    public boolean isConnected() {
        return connectionState == ProtocolConnectionState.CONNECTED;
    }
}
