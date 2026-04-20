package com.fbp.engine.protocol.mqtt.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MqttPublisherNodeTest {

    private static class TestMqttPublisherNode extends MqttPublisherNode {
        private TestMqttPublisherNode(String id, Map<String, Object> config) {
            super(id, config);
        }

        private Object readConfig(String key) {
            return getConfigValue(key);
        }
    }

    @Test
    @DisplayName("포트 구성: 입력 포트 in이 존재하는지(1)")
    void testInputPort() {
        // Given
        MqttPublisherNode node = new MqttPublisherNode("mqtt-publisher", publisherConfig());

        // When & Then
        assertNotNull(node.getInputPort("in"));
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 연결되지 않은 상태인지(2)")
    void testInitialState() {
        // Given
        MqttPublisherNode node = new MqttPublisherNode("mqtt-publisher", publisherConfig());

        // When & Then
        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("config 조회: topic 설정 값을 조회할 수 있는지(3)")
    void testConfigTopic() {
        // Given
        TestMqttPublisherNode node = new TestMqttPublisherNode("mqtt-publisher", publisherConfig());

        // When & Then
        assertEquals("alert/temp", node.readConfig("topic"));
    }

    private Map<String, Object> publisherConfig() {
        return Map.of(
                "clientId", "mqtt-publisher",
                "host", "localhost",
                "port", 1883,
                "topic", "alert/temp"
        );
    }
}
