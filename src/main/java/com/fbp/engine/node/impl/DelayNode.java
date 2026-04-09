package com.fbp.engine.node.impl;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

public class DelayNode extends AbstractNode {
    private final long delayMs;

    public DelayNode(String id, long delayMs) {
        super(id);
        this.delayMs = delayMs;
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        try {
            Thread.sleep(delayMs);
            send("out", portMessage.message());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
