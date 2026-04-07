package com.fbp.engine.node;

import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.core.DefaultOutputPort;
import com.fbp.engine.core.InputPort;
import com.fbp.engine.core.Node;
import com.fbp.engine.core.OutputPort;
import com.fbp.engine.message.Message;
import lombok.Getter;

public class FilterNode implements Node {
    private String id;
    private String key;
    private double threshold;
    @Getter
    private final InputPort inputPort;
    @Getter
    private final OutputPort outputPort;

    public FilterNode(String id, String key, double threshold) {
        this.id = id;
        this.key = key;
        this.threshold = threshold;
        this.inputPort = new DefaultInputPort("in", this);
        this.outputPort = new DefaultOutputPort("out");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(Message message) {
        Object value = message.get(key);
        if (value instanceof Number number && number.doubleValue() >= threshold) {
            outputPort.send(message);
        }
    }

    public InputPort getInputPort() {
        return inputPort;
    }

    public OutputPort getOutputPort() {
        return outputPort;
    }
}
