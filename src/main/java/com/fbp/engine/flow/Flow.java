package com.fbp.engine.flow;

import com.fbp.engine.connection.Connection;
import com.fbp.engine.node.AbstractNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Flow {
    private final String id;
    private final Map<String, AbstractNode> nodes;
    private final List<Connection> connections;

    public Flow(String id) {
        this.id = id;
        this.nodes = new HashMap<>();
        this.connections = new ArrayList<>();
    }

    public Flow addNode(AbstractNode node) {
        nodes.put(node.getId(), node);
        return this;
    }

    public Flow connect(String sourceNodeId, String sourcePort, String targetNodeId, String targetPort) {
        AbstractNode sourceNode = nodes.get(sourceNodeId);
        AbstractNode targetNode = nodes.get(targetNodeId);
        if (sourceNode == null
                || targetNode == null
                || sourceNode.getOutputPort(sourcePort) == null
                || targetNode.getInputPort(targetPort) == null) {
            throw new IllegalArgumentException("노드/포트가 없습니다");
        }

        Connection connection = new Connection(
                String.format("%s:%s->%s:%s", sourceNodeId, sourcePort, targetNodeId, targetPort));
        sourceNode.getOutputPort(sourcePort).connect(connection);
        connections.add(connection);

        return this;
    }

    public void initialize() {
        for (AbstractNode node : nodes.values()) {
            node.initialize();
        }
    }

    public void shutdown() {
        for (AbstractNode node : nodes.values()) {
            node.shutdown();
        }
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (nodes.isEmpty()) {
            errors.add("노드가 없습니다");
        }
        for (AbstractNode node : nodes.values()) {

        }

        return errors;
    }
}
