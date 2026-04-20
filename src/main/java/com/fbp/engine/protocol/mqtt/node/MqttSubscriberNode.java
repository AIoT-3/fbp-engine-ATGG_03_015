package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.NodeExecutionMode;
import com.fbp.engine.core.node.protocol.ProtocolNode;
import com.fbp.engine.protocol.mqtt.support.MqttPayloadMapper;
import com.fbp.engine.protocol.mqtt.support.MqttQosSupport;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.Mqtt5Subscribe;

import java.util.HashMap;
import java.util.Map;

public class MqttSubscriberNode extends ProtocolNode {
    // config keys
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String TOPIC_KEY = "topic";
    private static final String QOS_KEY = "qos";
    // default values
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 1883;
    private static final MqttQos DEFAULT_QOS = MqttQos.AT_LEAST_ONCE;

    private Mqtt5AsyncClient client;

    public MqttSubscriberNode(String id, Map<String, Object> config) {
        super(id, config);
        addOutputPort("out");
    }

    @Override
    public NodeExecutionMode executionMode() {
        return NodeExecutionMode.SELF_DRIVEN;
    }

    @Override
    protected void doConnect() {
        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier(getStringConfig(CLIENT_ID_KEY))
                .serverHost(getStringConfig(HOST_KEY, DEFAULT_HOST))
                .serverPort(getIntConfig(PORT_KEY, DEFAULT_PORT))
                .buildAsync();

        client.connect().join();

        Mqtt5Subscribe subscribe = Mqtt5Subscribe.builder()
                .topicFilter(getStringConfig(TOPIC_KEY))
                .qos(MqttQosSupport.from(getConfigValue(QOS_KEY), DEFAULT_QOS))
                .build();

        client.subscribe(subscribe, this::handlePublish).join();
    }

    @Override
    protected void doDisconnect() {
        if (client != null) {
            client.disconnect().join();
            client = null;
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
    }

    private void handlePublish(Mqtt5Publish publish) {
        Map<String, Object> payload = new HashMap<>(MqttPayloadMapper.toPayload(publish.getPayloadAsBytes()));
        payload.put("topic", publish.getTopic().toString());
        payload.put("mqttTimestamp", System.currentTimeMillis());
        send("out", Message.of(payload));
    }
}
