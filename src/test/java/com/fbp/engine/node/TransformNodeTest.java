package com.fbp.engine.node;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.impl.TransformNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransformNodeTest {

    @Test
    @DisplayName("onProcess: 입력 메시지가 변환되어 전달됨, 변환 후에도 원본 보존(1)(3)")
    void testOnProcess_TransformMessage() {
        // Given
        TransformNode transformNode = new TransformNode("transformNode1", message -> {
            String payload = message.get("testKey");
            return Message.of(Map.of("testKey", payload.toUpperCase()));
        });
        Message inputMessage = Message.of(Map.of("testKey", "wowow"));
        Connection connection = new Connection("transform-to-next");
        transformNode.getOutputPort("out").connect(connection);

        // When
        transformNode.process(inputMessage);
        Message result = connection.poll();

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("WOWOW", result.get("testKey")),
                () -> assertEquals("wowow", inputMessage.get("testKey"))
        );
    }

    @Test
    @DisplayName("onProcess: 변환 함수가 null을 반환하면 메시지가 전달되지 않음(2)")
    void testOnProcess_TransformToNull() {
        // Given
        TransformNode transformNode = new TransformNode("transformNode2", message -> null);
        Message inputMessage = Message.of(Map.of("testKey", "wowow"));
        Connection connection = new Connection("transform-to-next");
        transformNode.getOutputPort("out").connect(connection);

        // When
        transformNode.process(inputMessage);
        int bufferSize = connection.getBufferSize();

        // Then
        assertEquals(0, bufferSize);
    }
}