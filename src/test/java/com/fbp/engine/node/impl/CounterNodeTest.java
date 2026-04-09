package com.fbp.engine.node.impl;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CounterNodeTest {

    @Test
    @DisplayName("onProcess: 첫 번째 메시지 전달 후 count값이 1로 증가, 원본 메시지 유지(1)(3)")
    void testOnProcess_FirstMessage() {
        // Given
        CounterNode counterNode = new CounterNode("counterNode1");
        Message message = Message.of(Map.of("testKey", "testValue"));

        Connection connection = new Connection("counter-to-next");
        counterNode.getOutputPort("out").connect(connection);

        // When & Then
        counterNode.process(new PortMessage("in", message));
        assertAll(
                () -> assertEquals((Integer) 1, connection.poll().get("count")),
                () -> assertEquals("testValue", message.get("testKey"))
        );
    }

    @Test
    @DisplayName("onProcess: 3개 메시지 전달 후 count값이 3으로 증가, 원본 메시지 유지(2)(3)")
    void testOnProcess_MultipleMessages() {
        // Given
        CounterNode counterNode = new CounterNode("counterNode2");
        Message message = Message.of(Map.of("testKey", "testValue"));

        Connection connection = new Connection("counter-to-next");
        counterNode.getOutputPort("out").connect(connection);

        // When & Then
        for (AtomicInteger i = new AtomicInteger(1); i.get() <= 3; i.incrementAndGet()) {
            counterNode.process(new PortMessage("in", message));
            assertAll(
                    () -> assertEquals((Integer) i.get(), connection.poll().get("count")),
                    () -> assertEquals("testValue", message.get("testKey"))
            );
        }
    }
}
