package com.fbp.engine.core.port.impl;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.node.InboxNode;
import com.fbp.engine.core.port.InputPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultInputPort implements InputPort {
    private String name;
    private InboxNode owner;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receive(Message message) {
        owner.enqueueInput(name, message);
    }
}
