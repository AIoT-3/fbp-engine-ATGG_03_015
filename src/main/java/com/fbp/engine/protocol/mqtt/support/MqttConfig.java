package com.fbp.engine.protocol.mqtt.support;

import com.hivemq.client.mqtt.datatypes.MqttQos;

public final class MqttConfig {
    public static final String CLIENT_ID_KEY = "clientId";
    public static final String HOST_KEY = "host";
    public static final String PORT_KEY = "port";
    public static final String TOPIC_KEY = "topic";
    public static final String QOS_KEY = "qos";

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 1883;
    public static final MqttQos DEFAULT_QOS = MqttQos.AT_LEAST_ONCE;

    private MqttConfig() {
    }
}
