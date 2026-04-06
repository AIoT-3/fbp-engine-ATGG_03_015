package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ConnectionTest {

    @Mock
    private InputPort target;

    @Test
    @DisplayName("deliver: deliver()한 메시지가 target의 receive()를 통해 전달되는지(1)(3)")
    void testDeliver() {
        // Given
        Connection connection = new Connection("testConnection");
        Message message = Message.of(Map.of("key", "value"));
        connection.setTarget(target);

        // When
        connection.deliver(message);

        // Then
        assertEquals(0, connection.getBufferSize());
        then(target).should().receive(message);
    }

    @Test
    @DisplayName("target이 null인 경우: deliver() 해도 예외 발생하지 않음(2)(3)")
    void testDeliverWithNullTarget() {
        // Given
        Connection connection = new Connection("testConnection");
        Message message = Message.of(Map.of("key", "value"));

        // When & Then
        assertDoesNotThrow(() -> connection.deliver(message));
        assertEquals(1, connection.getBufferSize());
    }

    @Test
    @DisplayName("여러 메시지 전달: deliver()한 여러 메시지가 target의 receive()를 통해 순서대로 전달되는지(4)(3)")
    void testDeliverMultipleMessages() {
        // Given
        Connection connection = new Connection("testConnection");
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));
        connection.setTarget(target);

        // When
        connection.deliver(message1);
        connection.deliver(message2);

        // Then
        assertEquals(0, connection.getBufferSize());
        then(target).should().receive(message1);
        then(target).should().receive(message2);
    }
}
