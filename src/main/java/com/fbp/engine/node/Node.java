package com.fbp.engine.node;

import com.fbp.engine.message.Message;
import com.fbp.engine.port.InputPort;
import com.fbp.engine.port.OutputPort;

public interface Node {
    String getId();
    void process(Message message);
    void initialize();
    void shutdown();
    InputPort getInputPort(String name);
    OutputPort getOutputPort(String name);
}
