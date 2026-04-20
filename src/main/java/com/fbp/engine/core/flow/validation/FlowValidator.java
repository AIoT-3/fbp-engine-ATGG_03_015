package com.fbp.engine.core.flow.validation;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.edge.Edge;
import com.fbp.engine.core.flow.Flow;
import com.fbp.engine.core.node.model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FlowValidator {
    private FlowValidator() {
        /* This utility class should not be instantiated */
    }

    public static List<FlowValidationError> validate(Flow flow) {
        Map<String, Node> nodes = flow.getNodes();
        List<Edge> edges = flow.getEdges();
        List<FlowValidationError> errors = new ArrayList<>();

        // 1. 노드 존재 여부 검증
        try {
            FlowValidationSupport.validateFlowHasNodes(nodes);
        } catch (EngineException e) {
            errors.add(FlowValidationError.from(e));
            return errors;
        }

        // 2. 엣지 검증
        for (Edge edge : edges) {
            try {
                FlowValidationSupport.validateEdge(edge, nodes);
            } catch (EngineException e) {
                errors.add(FlowValidationError.from(e));
            }
        }

        // 3. 순환 참조 검증
        try {
            FlowValidationSupport.validateNoCycles(nodes, edges);
        } catch (EngineException e) {
            errors.add(FlowValidationError.from(e));
        }

        return errors;
    }
}
