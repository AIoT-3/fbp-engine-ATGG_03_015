package com.fbp.engine.node;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogNodeTest {

    @Test
    @DisplayName("onProcess: 수신한 메시지가 그대로 전달되고, 중간 삽입 노드로 동작하는지(1)(2)")
    void testOnProcess() {
        // Given
        LogNode logNode = new LogNode("logNode1");
        Connection connection = new Connection("log-to-next");
        logNode.getOutputPort("out").connect(connection);
        Message message = Message.of(Map.of("key", "value"));

        // When
        logNode.process(message);
        Message result = connection.poll();

        // Then
        assertEquals(message, result);
    }

}