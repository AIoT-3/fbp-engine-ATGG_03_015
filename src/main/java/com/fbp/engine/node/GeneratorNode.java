package com.fbp.engine.node;

import com.fbp.engine.core.Node;
import com.fbp.engine.core.OutputPort;
import com.fbp.engine.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class GeneratorNode implements Node {
    private String id;
    @Getter @Setter
    private OutputPort outputPort;

    public GeneratorNode(String id) {
        this.id = id;
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
}

