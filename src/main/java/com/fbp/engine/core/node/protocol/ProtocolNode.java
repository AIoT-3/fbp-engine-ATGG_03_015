package com.fbp.engine.core.node.protocol;

import com.fbp.engine.core.node.model.AbstractNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class ProtocolNode extends AbstractNode {
    private static final long DEFAULT_RETRY_INTERVAL_MS = 5000;
    private static final int DEFAULT_RETRY_COUNT = 10;
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
        connectWithRetry();
    }

    @Override
    public void shutdown() {
        try {
            doDisconnect();
        } finally {
            connectionState = ProtocolConnectionState.DISCONNECTED;
        }
    }

    protected abstract void doConnect();
    protected abstract void doDisconnect();

    private void connectWithRetry() {
        Object retryCountValue = getConfigValue(RETRY_COUNT_KEY);
        int retryCount;
        if (retryCountValue instanceof Number number) {
            retryCount = number.intValue();
        } else {
            retryCount = DEFAULT_RETRY_COUNT;
        }

        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                connectionState = ProtocolConnectionState.CONNECTING;
                doConnect();
                connectionState = ProtocolConnectionState.CONNECTED;
                return;
            } catch (RuntimeException e) {
                connectionState = ProtocolConnectionState.ERROR;
                if (attempt == retryCount) {
                    return;
                }

                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public boolean isConnected() {
        return connectionState == ProtocolConnectionState.CONNECTED;
    }

    public Object getConfigValue(String key) {
        return config.get(key);
    }

}
