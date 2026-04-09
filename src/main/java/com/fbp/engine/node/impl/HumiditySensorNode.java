package com.fbp.engine.node.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

import java.util.Map;

public class HumiditySensorNode extends AbstractNode {
    private final double min;
    private final double max;

    public HumiditySensorNode(String id, double min, double max) {
        super(id);
        this.min = min;
        this.max = max;
        addInputPort("trigger");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        double randomHumidity = min + Math.random() * (max - min);
        Message outputMessage = Message.of(Map.of(
                "sensorId", getId(),
                "humidity", Math.round(randomHumidity * 10.0) / 10.0,
                "unit", "%",
                "timestamp", System.currentTimeMillis()
        ));
        send("out", outputMessage);
    }
}
