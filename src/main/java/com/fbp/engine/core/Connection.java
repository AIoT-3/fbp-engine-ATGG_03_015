package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {
    private static final int DEFAULT_BUFFER_CAPACITY = 100;

    @Getter
    private final String id;
    private final BlockingQueue<Message> buffer;
    @Setter
    private InputPort target;

    public Connection(String id, int capacity) {
        this.id = id;
        this.buffer = new LinkedBlockingQueue<>(capacity);
    }

    public Connection(String id) {
        this(id, DEFAULT_BUFFER_CAPACITY);
    }

    public void deliver(Message message) {
        try {
            buffer.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("메시지 전달 중 인터럽트 발생", e);
        }
    }

    public Message poll() {
        try {
            return buffer.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("메시지 폴링 중 인터럽트 발생", e);
        }
    }

    public int getBufferSize() {
        return buffer.size();
    }
}
