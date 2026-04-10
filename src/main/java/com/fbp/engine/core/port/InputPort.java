package com.fbp.engine.core.port;

import com.fbp.engine.core.message.Message;

public interface InputPort {
    String getName();
    void receive(Message message);
}
