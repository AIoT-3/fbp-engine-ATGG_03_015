package com.fbp.engine.protocol.mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient.Mqtt5Publishes;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HiveMqttQuickStart {
    private static final String HOST = "localhost";
    private static final int PORT = 1883;
    private static final String TOPIC = "fbp/stage2/quickstart";
    private static final String PAYLOAD = "Hello HiveMQ";

    public static void main(String[] args) throws InterruptedException {
        Mqtt5BlockingClient client = MqttClient.builder()
                .useMqttVersion5()
                .identifier("fbp-quickstart-" + UUID.randomUUID())
                .serverHost(HOST)
                .serverPort(PORT)
                .buildBlocking();

        client.connect();
        try (Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            client.subscribeWith()
                    .topicFilter(TOPIC)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send();

            client.publishWith()
                    .topic(TOPIC)
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .payload(PAYLOAD.getBytes(StandardCharsets.UTF_8))
                    .send();

            publishes.receive(3, TimeUnit.SECONDS)
                    .ifPresent(publish -> System.out.println(new String(
                            publish.getPayloadAsBytes(),
                            StandardCharsets.UTF_8
                    )));
        } finally {
            client.disconnect();
        }
    }
}
