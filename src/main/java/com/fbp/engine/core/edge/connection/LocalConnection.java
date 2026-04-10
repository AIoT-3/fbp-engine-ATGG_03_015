package com.fbp.engine.core.edge.connection;

import com.fbp.engine.core.edge.exception.ConnectionDeliveryException;
import com.fbp.engine.core.edge.exception.ConnectionPollException;
import com.fbp.engine.core.message.Message;
import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalConnection implements Connection {
    private static final int DEFAULT_BUFFER_CAPACITY = 100;

    @Getter
    private final String id;
    private final BlockingQueue<Message> buffer;

    public LocalConnection(String id, int capacity) {
        this.id = id;
        this.buffer = new LinkedBlockingQueue<>(capacity);
    }

    public LocalConnection(String id) {
        this(id, DEFAULT_BUFFER_CAPACITY);
    }

    @Override
    public void deliver(Message message) {
        try {
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionDeliveryException(id, e);
        }
    }

    @Override
    public Message poll() {
        try {
            return buffer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionPollException(id, e);
        }
    }

    @Override
    public int getBufferSize() {
        return buffer.size();
    }

    public static Connection between(
            String sourceNodeId,
            String sourcePortName,
            String targetNodeId,
            String targetPortName
    ) {
        return new LocalConnection(String.format("%s:%s->%s:%s",
                sourceNodeId, sourcePortName, targetNodeId, targetPortName));
    }
}
