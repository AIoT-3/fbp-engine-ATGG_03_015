package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
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
        Connection connection = new LocalConnection("transform-to-next");
        transformNode.getOutputPort("out").connect(connection);

        // When
        transformNode.process(new PortMessage("in", inputMessage));
        Message result = connection.take();

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
        Connection connection = new LocalConnection("transform-to-next");
        transformNode.getOutputPort("out").connect(connection);

        // When
        transformNode.process(new PortMessage("in", inputMessage));
        int bufferSize = connection.getBufferSize();

        // Then
        assertEquals(0, bufferSize);
    }
}
