package com.fbp.engine.core.node.model;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.port.InputPort;
import com.fbp.engine.core.port.OutputPort;

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
