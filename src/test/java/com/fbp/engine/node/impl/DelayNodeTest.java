package com.fbp.engine.node.impl;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.edge.LocalConnection;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DelayNodeTest {

    @Test
    @DisplayName("onProcess: 메시지가 지정된 시간만큼 지연되어 전달됨, 지연 후에도 메시지 내용이 동일함(1)")
    void testOnProcess_DelayMessage() {
        // Given
        DelayNode delayNode = new DelayNode("delayNode1", 100);
        Message message = Message.of(Map.of("testKey", "testValue"));
        long startTime = System.currentTimeMillis();

        Connection connection = new LocalConnection("delay-to-next");
        delayNode.getOutputPort("out").connect(connection);

        // When
        delayNode.process(new PortMessage("in", message));
        Message result = connection.poll();
        long endTime = System.currentTimeMillis();

        // Then
        long elapsedTime = endTime - startTime;
        assertAll(
                () -> assertEquals("testValue", result.get("testKey")),
                () -> assertTrue(elapsedTime >= 100)
        );
    }
}
