package com.fbp.engine.port.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.node.Node;
import com.fbp.engine.port.InputPort;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultInputPort implements InputPort {
    private String name;
    private Node owner;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void receive(Message message) {
        owner.process(message);
    }
}
