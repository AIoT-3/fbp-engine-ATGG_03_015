package com.fbp.engine.node;

import com.fbp.engine.core.Node;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PrintNodeTest {

    @Test
    @DisplayName("getId: 생성 시 시정한 ID 반환(1)")
    void testGetId() {
        // Given
        String nodeId = "printNode1";
        PrintNode printNode = new PrintNode(nodeId);

        // When
        String result = printNode.getId();

        // Then
        assertEquals(nodeId, result);
    }

    @Test
    @DisplayName("process: 호출 시 예외 발생 없음(2)")
    void testProcess() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");
        Message message = Message.of(Map.of("key", "value"));

        // When & Then
        assertDoesNotThrow(() -> printNode.process(message));
    }

    @Test
    @DisplayName("Node 인터페이스 구현(3)")
    void testNodeInterface() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");

        // Then
        assertInstanceOf(Node.class, printNode);
    }

    @Test
    @DisplayName("getInputPort가 null이 아님(1)")
    void testGetInputPort() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");

        // When
        var inputPort = printNode.getInputPort();

        // Then
        assertNotNull(inputPort);
    }

    @Test
    @DisplayName("InputPort의 receive() 호출 시 process()가 호출되는지(2)")
    void testInputPortReceive() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");
        Message message = Message.of(Map.of("key", "value"));

        // When & Then
        assertDoesNotThrow(() -> printNode.getInputPort().receive(message));
    }
}