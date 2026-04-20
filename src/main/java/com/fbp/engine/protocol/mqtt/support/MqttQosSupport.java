package com.fbp.engine.protocol.mqtt.support;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.hivemq.client.mqtt.datatypes.MqttQos;

public final class MqttQosSupport {
    private MqttQosSupport() {
        /* utility class */
    }

    public static MqttQos from(Object value, MqttQos defaultValue) {
        return switch (value) {
            case null -> defaultValue;
            case MqttQos mqttQos -> mqttQos;
            case Number number -> fromNumber(number.intValue());
            default -> throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
        };
    }

    private static MqttQos fromNumber(int value) {
        return switch (value) {
            case 0 -> MqttQos.AT_MOST_ONCE;
            case 1 -> MqttQos.AT_LEAST_ONCE;
            case 2 -> MqttQos.EXACTLY_ONCE;
            default -> throw new EngineException(EngineFailureType.PROTOCOL_CONFIG_INVALID);
        };
    }
}
