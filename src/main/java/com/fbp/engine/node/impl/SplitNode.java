package com.fbp.engine.node.impl;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

public class SplitNode extends AbstractNode {
    private final String key;
    private final double threshold;

    public SplitNode(String id, String key, double threshold) {
        super(id);
        this.key = key;
        this.threshold = threshold;
        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        Object value = portMessage.message().get(key);
        if (!(value instanceof Number number)) {
            // 아 "패턴 변수"는 변수가 반드시 초기화되었다고 보장되는 경로에서만 보인다고
            // 이걸 판단하는 게 flow scoping이고
            return;
        }
        if (number.doubleValue() >= threshold) {
            send("match", portMessage.message());
        } else {
            send("mismatch", portMessage.message());
        }
    }
}
