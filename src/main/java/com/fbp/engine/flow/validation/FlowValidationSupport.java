package com.fbp.engine.flow.validation;

import com.fbp.engine.flow.Flow;
import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.exception.EmptyFlowException;
import com.fbp.engine.flow.exception.FlowNotFoundException;
import com.fbp.engine.node.Node;
import com.fbp.engine.node.exception.NodeNotFoundException;
import com.fbp.engine.port.exception.InputPortNotFoundException;
import com.fbp.engine.port.exception.OutputPortNotFoundException;

import java.util.Map;

public class FlowValidationSupport {
    private FlowValidationSupport() {
        /* This utility class should not be instantiated */
    }

    public static void validateFlowExists(Flow flow) {
        if (flow == null) {
            throw new FlowNotFoundException();
        }
    }

    public static void validateFlowHasNodes(Map<String, Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new EmptyFlowException();
        }
    }

    public static void validateNodeExists(Map<String, Node> nodes, String nodeId) {
        if (nodeId == null || nodeId.isEmpty()
                || !nodes.containsKey(nodeId)) {
            throw new NodeNotFoundException(nodeId);
        }
    }

    public static void validateInputPortExists(Node node, String portName) {
        if (node.getInputPort(portName) == null) {
            throw new InputPortNotFoundException(node.getId(), portName);
        }
    }

    public static void validateOutputPortExists(Node node, String portName) {
        if (node.getOutputPort(portName) == null) {
            throw new OutputPortNotFoundException(node.getId(), portName);
        }
    }

    public static void validateEdge(Edge edge, Map<String, Node> nodes) {
        validateNodeExists(nodes, edge.sourceNodeId());
        validateNodeExists(nodes, edge.targetNodeId());
        validateOutputPortExists(nodes.get(edge.sourceNodeId()), edge.sourcePortName());
        validateInputPortExists(nodes.get(edge.targetNodeId()), edge.targetPortName());
    }

    public static void validateNoCycles() {
    }
}
