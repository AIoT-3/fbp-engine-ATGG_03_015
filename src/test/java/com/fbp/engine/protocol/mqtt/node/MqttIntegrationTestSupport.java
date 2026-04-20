package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.message.Message;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

final class MqttIntegrationTestSupport {
    static final String HOST = "localhost";
    static final int PORT = 1883;

    private MqttIntegrationTestSupport() {
        /* test utility */
    }

    static void assumeBrokerRunning() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), 500);
        } catch (IOException | RuntimeException e) {
            Assumptions.assumeTrue(false, "MQTT broker가 여기서 작동하고 있지 않습니다. " + HOST + ":" + PORT);
        }
    }

    static String uniqueTopic(String suffix) {
        return "fbp/test/" + suffix + "/" + UUID.randomUUID();
    }

    static Mqtt5BlockingClient newBlockingClient(String prefix) {
        return MqttClient.builder()
                .useMqttVersion5()
                .identifier(prefix + "-" + UUID.randomUUID())
                .serverHost(HOST)
                .serverPort(PORT)
                .buildBlocking();
    }

    static Map<String, Object> mqttConfig(String clientIdPrefix, String topic) {
        return Map.of(
                "clientId", clientIdPrefix + "-" + UUID.randomUUID(),
                "host", HOST,
                "port", PORT,
                "topic", topic,
                "qos", 1
        );
    }

    static Message waitForMessage(Connection connection, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (connection.getBufferSize() > 0) {
                return connection.take();
            }
            Thread.sleep(20);
        }
        throw new AssertionError("MQTT message가 주어진 시간 안에 전달되지 않았습니다: " + timeout);
    }
}
