package com.fbp.engine.core.node.model;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;

public interface InboxNode extends Node {
    void enqueueInput(String inputPortName, Message message);
    PortMessage takeInput();
}
