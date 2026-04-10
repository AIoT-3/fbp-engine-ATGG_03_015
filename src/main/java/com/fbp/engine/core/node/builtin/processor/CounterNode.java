package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CounterNode extends AbstractNode {
    private int count = 0;

    public CounterNode(String id) {
        super(id);
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        send("out", portMessage.message().withEntry("count", ++count));
    }

    @Override
    public void shutdown() {
        log.info("[{}] 총 처리 메시지: {}건", this.getId(), count);
    }
}
