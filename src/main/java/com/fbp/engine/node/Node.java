package com.fbp.engine.node;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.port.InputPort;
import com.fbp.engine.port.OutputPort;

public interface Node {
    String getId();
    void process(PortMessage portMessage);
    void initialize();
    void shutdown();
    default NodeExecutionMode executionMode() {
        return NodeExecutionMode.POLLING;
    }
    InputPort getInputPort(String name);
    OutputPort getOutputPort(String name);
}
