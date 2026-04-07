package com.fbp.engine.node;

import com.fbp.engine.core.Node;
import com.fbp.engine.core.OutputPort;
import com.fbp.engine.core.DefaultOutputPort;
import com.fbp.engine.message.Message;
import lombok.Getter;

import java.util.Map;

public class GeneratorNode implements Node {
    private String id;
    @Getter
    private final OutputPort outputPort;

    public GeneratorNode(String id) {
        this.id = id;
        this.outputPort = new DefaultOutputPort("out");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(Message message) {

    }

    public void generate(String key, Object value) {
        Map<String, Object> payload = Map.of(key, value);
        Message message = Message.of(payload);
        outputPort.send(message);
    }

    public OutputPort getOutputPort() {
        return outputPort;
    }
}
