package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Setter;

import java.util.LinkedList;
import java.util.Queue;

public class Connection {
    private String id;
    private Queue<Message> buffer;
    @Setter
    private InputPort target;

    public Connection(String id) {
        this.id = id;
        this.buffer = new LinkedList<>();
    }

    public void deliver(Message message) {
        buffer.add(message);
        if (target == null) {
            return;
        }

        while (!buffer.isEmpty()) {
            Message msg = buffer.poll();
            target.receive(msg);
        }
    }

    public int getBufferSize() {
        return buffer.size();
    }
}
