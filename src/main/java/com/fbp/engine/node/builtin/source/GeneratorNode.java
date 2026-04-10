package com.fbp.engine.node.builtin.source;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

import java.util.Map;

public class GeneratorNode extends AbstractNode {

    public GeneratorNode(String id) {
        super(id);
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {

    }

    public void generate(String key, Object value) {
        Map<String, Object> payload = Map.of(key, value);
        Message message = Message.of(payload);
        getOutputPort("out").send(message);
    }
}
