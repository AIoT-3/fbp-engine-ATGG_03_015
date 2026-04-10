package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.node.AbstractNode;
import com.fbp.engine.core.node.Node;
import com.fbp.engine.core.node.builtin.sink.PrintNode;
import com.fbp.engine.core.port.InputPort;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
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
        assertDoesNotThrow(() -> printNode.process(new PortMessage("in", message)));
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
    @DisplayName("getInputPort(in)가 null이 아님(1)")
    void testGetInputPort() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");

        // When
        InputPort inputPort = printNode.getInputPort("in");

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
        assertDoesNotThrow(() -> printNode.getInputPort("in").receive(message));
    }

    @Test
    @DisplayName("instanceof: PrintNode가 AbstractNode의 인스턴스인지(3)")
    void testInstanceOfAbstractNode() {
        // Given
        PrintNode printNode = new PrintNode("printNode1");

        // Then
        assertInstanceOf(AbstractNode.class, printNode);
    }
}
