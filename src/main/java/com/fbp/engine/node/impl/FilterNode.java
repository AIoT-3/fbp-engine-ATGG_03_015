package com.fbp.engine.node.impl;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

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
    public void onProcess(PortMessage portMessage) {
        Object value = portMessage.message().get(key);
        if (value instanceof Number number && number.doubleValue() >= threshold) {
            getOutputPort("out").send(portMessage.message());
        }
    }
}
