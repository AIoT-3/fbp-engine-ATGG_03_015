package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;

public class ThresholdFilterNode extends AbstractNode {
    private final String fieldName;
    private final double threshold;

    public ThresholdFilterNode(String id, String fieldName, double threshold) {
        super(id);
        this.fieldName = fieldName;
        this.threshold = threshold;
        addInputPort("in");
        addOutputPort("alert");
        addOutputPort("normal");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        Object value = portMessage.message().get(fieldName);
        if (!(value instanceof Number number)) {
            return;
        }

        if (number.doubleValue() > threshold) {
            send("alert", portMessage.message());
        } else {
            send("normal", portMessage.message());
        }
    }
}
