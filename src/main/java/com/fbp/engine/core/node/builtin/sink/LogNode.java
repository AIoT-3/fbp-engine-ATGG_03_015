package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LogNode extends AbstractNode {

    public LogNode(String id) {
        super(id);
        addInputPort("in");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        log.info("[{}][{}] {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")), this.getId(), portMessage.message());
        send("out", portMessage.message());
    }
}
