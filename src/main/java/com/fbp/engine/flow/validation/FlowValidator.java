package com.fbp.engine.flow.validation;

import com.fbp.engine.exception.EngineException;
import com.fbp.engine.flow.FlowEdge;
import com.fbp.engine.node.AbstractNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowValidator {

    public static List<String> validate(Map<String, AbstractNode> nodes, List<FlowEdge> edges) {
        List<String> errors = new ArrayList<>();

        // 1. 노드 존재 여부 검증
        try {
            FlowValidationSupport.validateFlowHasNodes(nodes);
        } catch (EngineException e) {
            errors.add(e.getMessage());
            return errors;
        }

        // 2. 엣지 검증
        for (FlowEdge edge : edges) {
            try {
                FlowValidationSupport.validateEdge(edge, nodes);
            } catch (EngineException e) {
                errors.add(e.getMessage());
            }
        }

        // 3. 순환 참조 검증
        try {
            FlowValidationSupport.validateNoCycles();
        } catch (EngineException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }
}
