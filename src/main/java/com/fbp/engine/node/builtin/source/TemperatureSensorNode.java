package com.fbp.engine.node.builtin.source;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

import java.util.Map;

public class TemperatureSensorNode extends AbstractNode {
    private final double min;
    private final double max;

    public TemperatureSensorNode(String id, double min, double max) {
        super(id);
        this.min = min;
        this.max = max;
        addInputPort("trigger");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        double randomTemp = min + Math.random() * (max - min);
        Message outputMessage = Message.of(Map.of(
                "sensorId", getId(),
                "temperature", Math.round(randomTemp * 10.0) / 10.0,
                "unit", "°C",
                "timestamp", System.currentTimeMillis()
        ));
        send("out", outputMessage);
    }
}
