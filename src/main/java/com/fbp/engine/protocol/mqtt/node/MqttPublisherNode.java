package com.fbp.engine.protocol.mqtt.node;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.protocol.ProtocolNode;
import com.fbp.engine.protocol.mqtt.support.MqttPayloadMapper;
import com.fbp.engine.protocol.mqtt.support.MqttQosSupport;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import java.util.Map;

import static com.fbp.engine.protocol.mqtt.support.MqttConfig.*;

public class MqttPublisherNode extends ProtocolNode {
    private static final String RETAIN_KEY = "retain";
    private static final boolean DEFAULT_RETAIN = false;

    private Mqtt5BlockingClient client;

    public MqttPublisherNode(String id, Map<String, Object> config) {
        super(id, config);
        addInputPort("in");
    }

    @Override
    protected void doConnect() {
        client = MqttClient.builder()
                .useMqttVersion5()
                .identifier(getStringConfig(CLIENT_ID_KEY))
                .serverHost(getStringConfig(HOST_KEY, DEFAULT_HOST))
                .serverPort(getIntConfig(PORT_KEY, DEFAULT_PORT))
                .buildBlocking();

        client.connect();
    }

    @Override
    protected void doDisconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        client.publishWith()
                .topic(getStringConfig(TOPIC_KEY))
                .qos(MqttQosSupport.from(getConfigValue(QOS_KEY), DEFAULT_QOS))
                .retain(getBooleanConfig(RETAIN_KEY, DEFAULT_RETAIN))
                .payload(MqttPayloadMapper.toBytes(portMessage.message()))
                .send();
    }
}
