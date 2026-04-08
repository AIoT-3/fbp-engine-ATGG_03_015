package com.fbp.engine.port;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.message.Message;

public interface OutputPort {
    String getName();
    void connect(Connection connection);
    void send(Message message);
}
