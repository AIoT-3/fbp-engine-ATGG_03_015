package com.fbp.engine.flow;

import com.fbp.engine.message.Message;
import com.fbp.engine.node.AbstractNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlowTest {

    private static class TestNode extends AbstractNode {
        private boolean initialized;
        private boolean shutdown;

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
        public void initialize() {
            initialized = true;
        }

        @Override
        public void shutdown() {
            shutdown = true;
        }

        @Override
        public void onProcess(Message message) {
        }
    }

    @Test
    @DisplayName("addNode/connect: 노드 등록, 체이닝, 정상 연결(1)(2)(3)")
    void testAddNodeAndConnect() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);

        // When
        Flow result = flow.addNode(sourceNode)
                .addNode(targetNode)
                .connect("source", "out", "target", "in");

        // Then
        assertAll(
                () -> assertSame(flow, result),
                () -> assertDoesNotThrow(() -> flow.connect("source", "out", "target", "in"))
        );
    }

    @Test
    @DisplayName("connect: 존재하지 않는 source node면 예외(4)")
    void testConnectWithInvalidSourceNode() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode targetNode = new TestNode("target", true, false);
        flow.addNode(targetNode);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> flow.connect("source", "out", "target", "in"));
    }

    @Test
    @DisplayName("connect: 존재하지 않는 target node면 예외(5)")
    void testConnectWithInvalidTargetNode() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        flow.addNode(sourceNode);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> flow.connect("source", "out", "target", "in"));
    }

    @Test
    @DisplayName("connect: 존재하지 않는 source port면 예외(6)")
    void testConnectWithInvalidSourcePort() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);
        flow.addNode(sourceNode).addNode(targetNode);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> flow.connect("source", "없는포트", "target", "in"));
    }

    @Test
    @DisplayName("connect: 존재하지 않는 target port면 예외(7)")
    void testConnectWithInvalidTargetPort() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);
        flow.addNode(sourceNode).addNode(targetNode);

        // When & Then
        assertThrows(RuntimeException.class,
                () -> flow.connect("source", "out", "target", "없는포트"));
    }

    @Test
    @DisplayName("initialize/shutdown: 전체 노드 initialize, shutdown 호출(10)(11)")
    void testInitializeAndShutdown() {
        // Given
        Flow flow = new Flow("test-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);
        flow.addNode(sourceNode).addNode(targetNode);

        // When
        flow.initialize();
        flow.shutdown();

        // Then
        assertAll(
                () -> assertTrue(sourceNode.initialized),
                () -> assertTrue(targetNode.initialized),
                () -> assertTrue(sourceNode.shutdown),
                () -> assertTrue(targetNode.shutdown)
        );
    }
}
