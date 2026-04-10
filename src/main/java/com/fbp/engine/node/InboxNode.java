package com.fbp.engine.node;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;

public interface InboxNode extends Node {
    void enqueueInput(String inputPortName, Message message);
    PortMessage takeInput();
}
