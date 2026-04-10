package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CollectorNode extends AbstractNode {
    private final List<Message> collected;

    public CollectorNode(String id) {
        super(id);
        this.collected = new ArrayList<>();
        addInputPort("in");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        collected.add(portMessage.message());
    }

}
