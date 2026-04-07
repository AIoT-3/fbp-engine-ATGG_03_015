package com.fbp.engine.node;

import com.fbp.engine.core.AbstractNode;
import com.fbp.engine.message.Message;
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
    public void onProcess(Message message) {
        send("out", message.withEntry("count", ++count));
    }

    @Override
    public void shutdown() {
        log.info("[{}] 총 처리 메시지: {}건", this.getId(), count);
    }
}
