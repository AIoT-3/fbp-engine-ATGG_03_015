package com.fbp.engine.core.edge.connection;

import com.fbp.engine.core.message.Message;

public interface Connection {
    String getId();

    void deliver(Message message);

    Message poll();

    int getBufferSize();
}
