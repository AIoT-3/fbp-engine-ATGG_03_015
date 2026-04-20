package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.protocol.mqtt.support.MqttPayloadMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MqttSubscriberNodeTest {

    private static class TestMqttSubscriberNode extends MqttSubscriberNode {
        private TestMqttSubscriberNode(String id, Map<String, Object> config) {
            super(id, config);
        }

        private Object readConfig(String key) {
            return getConfigValue(key);
        }
    }

    @Test
    @DisplayName("포트 구성: 출력 포트 out이 존재하는지(1)")
    void testOutputPort() {
        // Given
        MqttSubscriberNode node = new MqttSubscriberNode("mqtt-subscriber", subscriberConfig());

        // When & Then
        assertNotNull(node.getOutputPort("out"));
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 연결되지 않은 상태인지(2)")
    void testInitialState() {
        // Given
        MqttSubscriberNode node = new MqttSubscriberNode("mqtt-subscriber", subscriberConfig());

        // When & Then
        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("config 조회: topic 설정 값을 조회할 수 있는지(3)")
    void testConfigTopic() {
        // Given
        TestMqttSubscriberNode node = new TestMqttSubscriberNode("mqtt-subscriber", subscriberConfig());

        // When & Then
        assertEquals("sensor/temp", node.readConfig("topic"));
    }

    @Test
    @DisplayName("payload 변환: JSON 문자열을 Map으로 변환할 수 있는지(4)")
    void testJsonPayloadMapping() {
        // Given
        byte[] payload = "{\"value\":28.5,\"unit\":\"C\"}".getBytes(StandardCharsets.UTF_8);

        // When
        Map<String, Object> result = MqttPayloadMapper.toPayload(payload);

        // Then
        assertAll(
                () -> assertEquals(28.5, result.get("value")),
                () -> assertEquals("C", result.get("unit"))
        );
    }

    @Test
    @DisplayName("payload 변환: JSON 파싱 실패 시 rawPayload로 원문을 보존하는지(5)")
    void testRawPayloadFallback() {
        // Given
        byte[] payload = "not-json".getBytes(StandardCharsets.UTF_8);

        // When
        Map<String, Object> result = MqttPayloadMapper.toPayload(payload);

        // Then
        assertEquals("not-json", result.get("rawPayload"));
    }

    private Map<String, Object> subscriberConfig() {
        return Map.of(
                "clientId", "mqtt-subscriber",
                "host", "localhost",
                "port", 1883,
                "topic", "sensor/temp"
        );
    }
}
