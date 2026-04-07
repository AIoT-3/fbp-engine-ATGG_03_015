package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;

import java.util.Map;

public class GeneratorNode extends AbstractNode {

    public GeneratorNode(String id) {
        super(id);
        addOutputPort("out");
    }

    @Override
    public void onProcess(Message message) {

    }

    public void generate(String key, Object value) {
        Map<String, Object> payload = Map.of(key, value);
        Message message = Message.of(payload);
        getOutputPort("out").send(message);
    }
}
