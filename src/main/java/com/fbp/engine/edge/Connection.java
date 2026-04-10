package com.fbp.engine.edge;

import com.fbp.engine.message.Message;

public interface Connection {
    String getId();

    void deliver(Message message);

    Message poll();

    int getBufferSize();
}
