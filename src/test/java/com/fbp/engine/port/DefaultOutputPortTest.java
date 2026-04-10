package com.fbp.engine.port;

import com.fbp.engine.edge.connection.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.port.impl.DefaultOutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultOutputPortTest {

    @Mock
    private Connection connection1;

    @Mock
    private Connection connection2;

    @Test
    @DisplayName("send: 단일 Connection 전달(1)")
    void testSingleConnectionSend() {
        // Given
        DefaultOutputPort outputPort = new DefaultOutputPort("testOutputPort");
        outputPort.connect(connection1);
        Message message = Message.of(Map.of("key", "value"));

        // When
        outputPort.send(message);

        // Then
        then(connection1).should().deliver(message);
    }

    @Test
    @DisplayName("send: 여러 Connection 전달(2)")
    void testMultipleConnectionsSend() {
        // Given
        DefaultOutputPort outputPort = new DefaultOutputPort("testOutputPort");
        outputPort.connect(connection1);
        outputPort.connect(connection2);
        Message message = Message.of(Map.of("key", "value"));

        // When
        outputPort.send(message);

        // Then
        then(connection1).should().deliver(message);
        then(connection2).should().deliver(message);
    }

    @Test
    @DisplayName("send: connect() 없이 send() 호출 시 예외 발생하지 않음(3)")
    void testSendWithoutConnections() {
        // Given
        DefaultOutputPort outputPort = new DefaultOutputPort("testOutputPort");
        Message message = Message.of(Map.of("key", "value"));

        // When & Then
        assertDoesNotThrow(() -> outputPort.send(message));
    }
}
