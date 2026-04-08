package com.fbp.engine.flow.validation;

import com.fbp.engine.exception.EngineException;
import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowValidator {
    private FlowValidator() {
        /* This utility class should not be instantiated */
    }

    public static List<String> validate(Flow flow) {
        Map<String, Node> nodes = flow.getNodes();
        List<Edge> edges = flow.getEdges();
        List<String> errors = new ArrayList<>();

        // 1. 노드 존재 여부 검증
        try {
            FlowValidationSupport.validateFlowHasNodes(nodes);
        } catch (EngineException e) {
            errors.add(e.getMessage());
            return errors;
        }

        // 2. 엣지 검증
        for (Edge edge : edges) {
            try {
                FlowValidationSupport.validateEdge(edge, nodes);
            } catch (EngineException e) {
                errors.add(e.getMessage());
            }
        }

        // 3. 순환 참조 검증
        try {
            FlowValidationSupport.validateNoCycles(nodes, edges);
        } catch (EngineException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }
}
