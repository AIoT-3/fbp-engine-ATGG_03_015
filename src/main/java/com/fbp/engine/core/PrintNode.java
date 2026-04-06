package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintNode implements Node{
    private String id;

    public PrintNode(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(Message message) {
        log.info("[{}] {}", id, message);
    }
}
