package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class MqttSubscriberNodeIntegrationTest {

    @Test
    @DisplayName("통합: Broker 연결 성공 후 shutdown 시 DISCONNECTED로 변경되는지(6)(9)")
    void testBrokerConnectionAndShutdown() {
        // Given
        MqttIntegrationTestSupport.assumeBrokerRunning();
        MqttSubscriberNode node = new MqttSubscriberNode("mqtt-subscriber",
                MqttIntegrationTestSupport.mqttConfig("mqtt-subscriber", MqttIntegrationTestSupport.uniqueTopic("subscriber")));

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
    @DisplayName("통합: Broker에서 수신한 메시지를 FBP Message로 변환하고 topic을 포함하는지(7)(8)")
    void testReceiveMessageWithTopic() throws InterruptedException {
        // Given
        MqttIntegrationTestSupport.assumeBrokerRunning();
        String topic = MqttIntegrationTestSupport.uniqueTopic("subscriber");
        MqttSubscriberNode node = new MqttSubscriberNode("mqtt-subscriber",
                MqttIntegrationTestSupport.mqttConfig("mqtt-subscriber", topic));
        Connection output = new LocalConnection("mqtt-subscriber-out");
        node.getOutputPort("out").connect(output);
        Mqtt5BlockingClient publisher = MqttIntegrationTestSupport.newBlockingClient("mqtt-publisher");
        publisher.connect();

        try {
            // When
            node.initialize();
            publisher.publishWith()
                    .topic(topic)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .payload("{\"value\":28.5}".getBytes(StandardCharsets.UTF_8))
                    .send();
            Message received = MqttIntegrationTestSupport.waitForMessage(output, Duration.ofSeconds(3));

            // Then
            assertAll(
                    () -> assertEquals(28.5, received.get("value")),
                    () -> assertEquals(topic, received.get("topic"))
            );
        } finally {
            node.shutdown();
            publisher.disconnect();
        }
    }
}
