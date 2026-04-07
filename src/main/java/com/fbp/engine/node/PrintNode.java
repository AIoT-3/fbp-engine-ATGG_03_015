package com.fbp.engine.node;

import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.core.InputPort;
import com.fbp.engine.core.Node;
import com.fbp.engine.message.Message;
import lombok.Getter;

public class PrintNode implements Node {
    private String id;
    @Getter
    private InputPort inputPort;

    public PrintNode(String id) {
        this.id = id;
        this.inputPort = new DefaultInputPort("in", this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(Message message) {
        System.out.printf("[%s] %s%n", id, message);
    }

    public InputPort getInputPort() {
        return inputPort;
    }
}
