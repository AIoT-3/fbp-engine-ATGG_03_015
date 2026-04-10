package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.AbstractNode;

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
