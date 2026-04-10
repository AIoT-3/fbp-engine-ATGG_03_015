package com.fbp.engine.node.builtin.sink;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintNode extends AbstractNode {

    public PrintNode(String id) {
        super(id);
        addInputPort("in");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        log.info("[{}] {}", this.getId(), portMessage.message());
    }
}
