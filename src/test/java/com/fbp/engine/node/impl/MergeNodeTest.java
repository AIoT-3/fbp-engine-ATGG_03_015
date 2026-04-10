package com.fbp.engine.node.impl;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.edge.LocalConnection;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MergeNodeTest {

    @Test
    @DisplayName("onProcess: 양쪽 입력 수신 후 합쳐진 메시지가 출력되는지(1)(2)")
    void testOnProcess_MergeMessages() {
        // Given
        MergeNode mergeNode = new MergeNode("mergeNode");
        Connection connection = new LocalConnection("merge-to-next");
        mergeNode.getOutputPort("out").connect(connection);
        Message message1 = Message.of(Map.of("temperature", 25.0));
        Message message2 = Message.of(Map.of("humidity", 60.0));

        // When
        mergeNode.process(new PortMessage("in-1", message1));
        mergeNode.process(new PortMessage("in-2", message2));
        Message result = connection.poll();

        // Then
        assertAll(
                () -> assertEquals(25.0, result.get("temperature")),
                () -> assertEquals(60.0, result.get("humidity"))
        );
    }

    @Test
    @DisplayName("onProcess: 한쪽 입력만 도착하면 출력이 발생하지 않는지(3)")
    void testOnProcess_WaitsForMatchingInput() {
        // Given
        MergeNode mergeNode = new MergeNode("mergeNode");
        Connection connection = new LocalConnection("merge-to-next");
        mergeNode.getOutputPort("out").connect(connection);

        // When
        mergeNode.process(new PortMessage("in-1", Message.of(Map.of("temperature", 25.0))));

        // Then
        assertEquals(0, connection.getBufferSize());
    }

    @Test
    @DisplayName("포트 구성: in-1, in-2, out이 모두 존재하는지(4)")
    void testGetPorts() {
        // Given
        MergeNode mergeNode = new MergeNode("mergeNode");

        // Then
        assertAll(
                () -> assertNotNull(mergeNode.getInputPort("in-1")),
                () -> assertNotNull(mergeNode.getInputPort("in-2")),
                () -> assertNotNull(mergeNode.getOutputPort("out"))
        );
    }
}
