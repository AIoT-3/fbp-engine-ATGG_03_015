package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.protocol.mqtt.support.MqttPayloadMapper;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class MqttPublisherNodeIntegrationTest {

    @Test
    @DisplayName("통합: Broker 연결 성공 후 shutdown 시 DISCONNECTED로 변경되는지(4)(7)")
    void testBrokerConnectionAndShutdown() {
        // Given
        MqttIntegrationTestSupport.assumeBrokerRunning();
        MqttPublisherNode node = new MqttPublisherNode("mqtt-publisher",
                MqttIntegrationTestSupport.mqttConfig("mqtt-publisher", MqttIntegrationTestSupport.uniqueTopic("publisher")));

        try {
            // When
            node.initialize();

            // Then
            assertTrue(node.isConnected());
        } finally {
            node.shutdown();
        }

        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("통합: FBP Message를 MQTT Broker로 발행할 수 있는지(5)")
    void testPublishMessage() throws InterruptedException {
        // Given
        MqttIntegrationTestSupport.assumeBrokerRunning();
        String topic = MqttIntegrationTestSupport.uniqueTopic("publisher");
        MqttPublisherNode node = new MqttPublisherNode("mqtt-publisher",
                MqttIntegrationTestSupport.mqttConfig("mqtt-publisher", topic));
        Mqtt5BlockingClient receiver = MqttIntegrationTestSupport.newBlockingClient("mqtt-receiver");
        receiver.connect();

        try (Mqtt5Publishes publishes = receiver.publishes(MqttGlobalPublishFilter.ALL)) {
            receiver.subscribeWith()
                    .topicFilter(topic)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send();

            // When
            node.initialize();
            node.process(new PortMessage("in", Message.of(Map.of("value", 31.5, "unit", "C"))));
            Optional<Mqtt5Publish> received = publishes.receive(3, TimeUnit.SECONDS);

            // Then
            assertTrue(received.isPresent());
            Map<String, Object> payload = MqttPayloadMapper.toPayload(received.get().getPayloadAsBytes());
            assertAll(
                    () -> assertEquals(31.5, payload.get("value")),
                    () -> assertEquals("C", payload.get("unit"))
            );
        } finally {
            node.shutdown();
            receiver.disconnect();
        }
    }
}
