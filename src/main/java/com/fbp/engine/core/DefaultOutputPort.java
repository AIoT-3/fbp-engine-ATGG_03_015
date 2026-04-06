package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultOutputPort implements OutputPort{
    private String name;
    private List<Connection> connections;

    public DefaultOutputPort(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void connect(@NonNull Connection connection) {
        connections.add(connection);
    }

    @Override
    public void send(Message message) {
        for (Connection connection : connections) {
            connection.deliver(message);
        }
    }
}
