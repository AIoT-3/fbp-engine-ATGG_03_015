package com.fbp.engine.core.port;

import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.node.model.InboxNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class DefaultInputPortTest {

    @Mock
    private InboxNode owner;

    @Test
    @DisplayName("receive: receive()가 owner의 process()를 호출하는지(1)")
    void testReceive() {
        // Given
        DefaultInputPort inputPort = new DefaultInputPort("testInputPort", owner);
        Message message = Message.of(Map.of("key", "value"));

        // When
        inputPort.receive(message);

        // Then
        then(owner).should().enqueueInput("testInputPort", message);
    }

    @Test
    @DisplayName("getName: getName()이 포트 이름을 반환하는지(2)")
    void testGetName() {
        // Given
        String portName = "testInputPort";
        DefaultInputPort inputPort = new DefaultInputPort(portName, owner);

        // When
        String result = inputPort.getName();

        // Then
        assertEquals(portName, result);
    }
}
