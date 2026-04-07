package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AbstractNodeTest {

    private static class TestNode extends AbstractNode {
        private Message processedMessage;

        public TestNode(String id) {
            super(id);
        }

        @Override
        public void onProcess(Message message) {
            this.processedMessage = message;
        }
    }

    @Test
    @DisplayName("getId: 생성 시 지정한 ID 반환(1)")
    void testGetId() {
        // Given
        String nodeId = "testNode1";
        TestNode node = new TestNode(nodeId);

        // When
        String result = node.getId();

        // Then
        assertEquals(nodeId, result);
    }

    @Test
    @DisplayName("addInputPort: 입력 포트 추가 후 getInputPort로 조회 가능(2)")
    void testAddInputPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // When
        node.addInputPort("in");
        InputPort inputPort = node.getInputPort("in");

        // Then
        assertNotNull(inputPort);
    }

    @Test
    @DisplayName("addOutputPort: 출력 포트 추가 후 getOutputPort로 조회 가능(3)")
    void testAddOutputPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // When
        node.addOutputPort("out");
        OutputPort outputPort = node.getOutputPort("out");

        // Then
        assertNotNull(outputPort);
    }

    @Test
    @DisplayName("미등록 포트 조회 시 null 반환(4)")
    void testGetNonexistentPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // Then
        assertNull(node.getInputPort("없는포트"));
        assertNull(node.getOutputPort("없는포트"));
    }

    @Test
    @DisplayName("process: 호출 시 onProcess가 호출되는지(5)")
    void testProcessCallsOnProcess() {
        // Given
        TestNode node = new TestNode("testNode1");
        Message message = Message.of(Map.of("key", "value"));

        // When
        node.process(message);

        // Then
        assertEquals(message, node.processedMessage);
    }

    @Test
    @DisplayName("send: OutputPort로 Connection 연결 후 send() 하면 상대측에서 수신(6)")
    void testSendDeliversToConnection() {
        // Given
        TestNode node = new TestNode("testNode1");
        node.addOutputPort("out");
        Connection connection = new Connection("testConnection");
        node.getOutputPort("out").connect(connection);
        Message message = Message.of(Map.of("key", "value"));

        // When
        node.send("out", message);
        Message receivedMessage = connection.poll();

        // Then
        assertEquals(message, receivedMessage);
    }

}
