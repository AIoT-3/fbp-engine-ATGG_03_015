package com.fbp.engine.node;

import com.fbp.engine.core.InputPort;
import com.fbp.engine.core.Node;
import com.fbp.engine.core.OutputPort;
import com.fbp.engine.message.Message;
import lombok.Getter;
import lombok.Setter;

public class FilterNode implements Node {
    private String id;
    private String key;
    private double threshold;
    @Getter @Setter
    private InputPort inputPort;
    @Getter @Setter
    private OutputPort outputPort;

    public FilterNode(String id, String key, double threshold) {
        this.id = id;
        this.key = key;
        this.threshold = threshold;
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
}
