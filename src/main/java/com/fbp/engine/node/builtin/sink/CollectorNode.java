package com.fbp.engine.node.builtin.sink;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
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
