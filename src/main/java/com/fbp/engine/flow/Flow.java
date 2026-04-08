package com.fbp.engine.flow;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.validation.FlowValidator;
import com.fbp.engine.flow.validation.FlowValidationSupport;
import com.fbp.engine.node.Node;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Flow {
    private final String id;
    private final Map<String, Node> nodes;
    private final List<Edge> edges;

    public Flow(String id) {
        this.id = id;
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
    }

    public Flow addNode(Node node) {
        nodes.put(node.getId(), node);
        return this;
    }

    public Flow connect(String sourceNodeId, String sourcePort, String targetNodeId, String targetPort) {
        // 1. Node 검증
        FlowValidationSupport.validateFlowHasNodes(nodes);
        FlowValidationSupport.validateNodeExists(nodes, sourceNodeId);
        FlowValidationSupport.validateNodeExists(nodes, targetNodeId);

        // 2. Port 검증
        Node sourceNode = nodes.get(sourceNodeId);
        Node targetNode = nodes.get(targetNodeId);
        FlowValidationSupport.validateOutputPortExists(sourceNode, sourcePort);
        FlowValidationSupport.validateInputPortExists(targetNode, targetPort);

        // 3. Connection 생성, 연결, 등록
        Connection connection = Connection.between(sourceNodeId, sourcePort, targetNodeId, targetPort);
        sourceNode.getOutputPort(sourcePort).connect(connection);
        edges.add(new Edge(sourceNodeId, sourcePort, targetNodeId, targetPort, connection));

        return this;
    }

    public void initialize() {
        for (Node node : nodes.values()) {
            node.initialize();
        }
    }

    public void shutdown() {
        for (Node node : nodes.values()) {
            node.shutdown();
        }
    }

    public List<String> validate() {
        return FlowValidator.validate(this);
    }
}
