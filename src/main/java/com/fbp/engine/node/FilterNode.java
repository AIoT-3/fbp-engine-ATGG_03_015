package com.fbp.engine.node;

import com.fbp.engine.core.*;
import com.fbp.engine.message.Message;

public class FilterNode extends AbstractNode {
    private String key;
    private double threshold;

    public FilterNode(String id, String key, double threshold) {
        super(id);
        this.key = key;
        this.threshold = threshold;
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    public void onProcess(Message message) {
        Object value = message.get(key);
        if (value instanceof Number number && number.doubleValue() >= threshold) {
            getOutputPort("out").send(message);
        }
    }
}
