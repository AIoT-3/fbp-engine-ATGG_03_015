package com.fbp.engine.port;

import com.fbp.engine.message.Message;

public interface InputPort {
    String getName();
    void receive(Message message);
}
