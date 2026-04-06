package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultInputPort implements InputPort{
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
