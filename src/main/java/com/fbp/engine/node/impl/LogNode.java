package com.fbp.engine.node.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.node.AbstractNode;
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
    public void onProcess(Message message) {
        log.info("[{}][{}] {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")), this.getId(), message);
        send("out", message);
    }
}
