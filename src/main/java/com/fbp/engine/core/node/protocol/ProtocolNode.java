package com.fbp.engine.core.node.protocol;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.node.model.AbstractNode;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

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
        this.config = Objects.requireNonNullElse(config, Map.of());
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
        int retryCount = getIntConfig(RETRY_COUNT_KEY, DEFAULT_RETRY_COUNT);
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                connectionState = ProtocolConnectionState.CONNECTING;
                doConnect();
                connectionState = ProtocolConnectionState.CONNECTED;
                return;
            } catch (RuntimeException e) {
                connectionState = ProtocolConnectionState.ERROR;
                if (attempt == retryCount) {
                    throw e;
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

    protected Object getConfigValue(String key) {
        return config.get(key);
    }

    protected String getStringConfig(String key) {
        Object value = getConfigValue(key);
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
    }

    protected String getStringConfig(String key, String defaultValue) {
        Object value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            return stringValue;
        }
        throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
    }

    protected int getIntConfig(String key, int defaultValue) {
        Object value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
    }

    protected double getDoubleConfig(String key, double defaultValue) {
        Object value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
    }

    protected boolean getBooleanConfig(String key, boolean defaultValue) {
        Object value = getConfigValue(key);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
    }


}
