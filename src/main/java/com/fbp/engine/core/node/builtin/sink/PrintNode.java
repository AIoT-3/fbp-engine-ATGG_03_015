package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;
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
