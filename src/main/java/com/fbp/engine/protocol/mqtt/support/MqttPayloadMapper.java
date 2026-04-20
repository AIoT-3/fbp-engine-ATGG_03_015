package com.fbp.engine.protocol.mqtt.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class MqttPayloadMapper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private MqttPayloadMapper() {
        /* utility class */
    }

    public static byte[] toBytes(Message message) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(message.payload());
        } catch (JsonProcessingException e) {
            throw new EngineException(EngineFailureType.PROTOCOL_MESSAGE_SEND_FAILED, e);
        }
    }

    public static Map<String, Object> toPayload(byte[] payload) {
        try {
            return OBJECT_MAPPER.readValue(payload, new TypeReference<>() {});
        } catch (IOException e) {
            return Map.of("rawPayload", new String(payload, StandardCharsets.UTF_8));
        }
    }
}
