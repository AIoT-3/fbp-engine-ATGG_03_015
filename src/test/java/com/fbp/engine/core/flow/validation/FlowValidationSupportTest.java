package com.fbp.engine.core.flow.validation;

import com.fbp.engine.core.edge.Edge;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.flow.Flow;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;
import com.fbp.engine.core.node.model.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;
import java.util.Map;

import static com.fbp.engine.core.exception.EngineFailureType.*;
import static org.junit.jupiter.api.Assertions.*;

class FlowValidationSupportTest {

    private static class TestNode extends AbstractNode {
        private TestNode(String id, boolean withInputPort, boolean withOutputPort) {
            super(id);
            if (withInputPort) {
                addInputPort("in");
            }
            if (withOutputPort) {
                addOutputPort("out");
            }
        }

        @Override
        public void onProcess(PortMessage portMessage) {
        }
    }

    @Test
    @DisplayName("flow: null Flow와 빈 노드 맵이면 예외(1)(2)")
    void testValidateFlowAndNodes() {
        // Given
        Flow flow = null;
        Map<String, Node> emptyNodes = Map.of();

        // When & Then
        assertAll(
                () -> assertEngineFailure(FLOW_NOT_FOUND,
                        () -> FlowValidationSupport.validateFlowExists(flow)),
                () -> assertEngineFailure(EMPTY_FLOW,
                        () -> FlowValidationSupport.validateFlowHasNodes(emptyNodes))
        );
    }

    @Test
    @DisplayName("node/port: 없는 노드, 입력 포트, 출력 포트면 예외(3)(4)(5)")
    void testValidateNodeAndPorts() {
        // Given
        TestNode inputOnlyNode = new TestNode("input", true, false);
        TestNode outputOnlyNode = new TestNode("output", false, true);
        Map<String, Node> nodes = Map.of("input", inputOnlyNode, "output", outputOnlyNode);

        // When & Then
        assertAll(
                () -> assertEngineFailure(NODE_NOT_FOUND,
                        () -> FlowValidationSupport.validateNodeExists(nodes, "missing")),
                () -> assertEngineFailure(INPUT_PORT_NOT_FOUND,
                        () -> FlowValidationSupport.validateInputPortExists(outputOnlyNode, "in")),
                () -> assertEngineFailure(OUTPUT_PORT_NOT_FOUND,
                        () -> FlowValidationSupport.validateOutputPortExists(inputOnlyNode, "out"))
        );
    }

    @Test
    @DisplayName("edge: 잘못된 엣지면 예외, 정상 엣지는 통과(6)(7)")
    void testValidateEdge() {
        // Given
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);
        Map<String, Node> nodes = Map.of("source", sourceNode, "target", targetNode);
        Edge validEdge = new Edge("source", "out", "target", "in",
                LocalConnection.between("source", "out", "target", "in"));
        Edge invalidEdge = new Edge("source", "없는포트", "target", "in",
                LocalConnection.between("source", "없는포트", "target", "in"));

        // When & Then
        assertAll(
                () -> assertDoesNotThrow(() -> FlowValidationSupport.validateEdge(validEdge, nodes)),
                () -> assertEngineFailure(OUTPUT_PORT_NOT_FOUND,
                        () -> FlowValidationSupport.validateEdge(invalidEdge, nodes))
        );
    }

    @Test
    @DisplayName("noCycles: 순환 없으면 통과, 순환 있으면 예외(8)(9)")
    void testValidateNoCycles() {
        // Given
        Map<String, Node> nodes = Map.of(
                "A", new TestNode("A", true, true),
                "B", new TestNode("B", true, true),
                "C", new TestNode("C", true, true)
        );
        List<Edge> acyclicEdges = List.of(
                new Edge("A", "out", "B", "in", LocalConnection.between("A", "out", "B", "in")),
                new Edge("B", "out", "C", "in", LocalConnection.between("B", "out", "C", "in"))
        );
        List<Edge> cyclicEdges = List.of(
                new Edge("A", "out", "B", "in", LocalConnection.between("A", "out", "B", "in")),
                new Edge("B", "out", "C", "in", LocalConnection.between("B", "out", "C", "in")),
                new Edge("C", "out", "A", "in", LocalConnection.between("C", "out", "A", "in"))
        );

        // When & Then
        assertAll(
                () -> assertDoesNotThrow(() -> FlowValidationSupport.validateNoCycles(nodes, acyclicEdges)),
                () -> assertEngineFailure(CYCLE_DETECTED,
                        () -> FlowValidationSupport.validateNoCycles(nodes, cyclicEdges))
        );
    }

    private void assertEngineFailure(EngineFailureType failureType, Executable executable) {
        EngineException exception = assertThrows(EngineException.class, executable);
        assertEquals(failureType, exception.getFailureType());
    }
}
