package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;

import java.util.function.Function;

public class TransformNode extends AbstractNode {
    private final Function<Message, Message> transformer;

    public TransformNode(String id, Function<Message, Message> transformer) {
        super(id);
        this.transformer = transformer;
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        Message result = transformer.apply(portMessage.message());
        if (result != null) {
            send("out", result);
        }
    }
}
