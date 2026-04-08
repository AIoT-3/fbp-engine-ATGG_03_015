package com.fbp.engine.node;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.port.OutputPort;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.impl.GeneratorNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

@ExtendWith(MockitoExtension.class)
class GeneratorNodeTest {

    @Mock
    private Connection mockConnection;

    @Test
    @DisplayName("generate: OutputPort의 send()가 호출되고, 메시지에 key와 value가 포함되는지(1)(2)")
    void testGenerate() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("testNode");
        generatorNode.getOutputPort("out").connect(mockConnection);

        // When
        generatorNode.generate("testKey", "testValue");

        // Then
        then(mockConnection).should().deliver(any(Message.class));
        then(mockConnection).should().deliver(argThat(message ->
                "testValue".equals(message.get("testKey"))
        ));
    }

    @Test
    @DisplayName("generate: 3번 호출 시 3개의 메시지가 순차적으로 OutputPort로 전달되는지(4)")
    void testGenerateMultipleTimes() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("testNode");
        generatorNode.getOutputPort("out").connect(mockConnection);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        // When
        generatorNode.generate("key1", "value1");
        generatorNode.generate("key2", "value2");
        generatorNode.generate("key3", "value3");

        // Then
        then(mockConnection).should(times(3)).deliver(messageCaptor.capture());
        assertAll(
                () -> assertEquals("value1", messageCaptor.getAllValues().get(0).get("key1")),
                () -> assertEquals("value2", messageCaptor.getAllValues().get(1).get("key2")),
                () -> assertEquals("value3", messageCaptor.getAllValues().get(2).get("key3"))
        );
    }

    @Test
    @DisplayName("getOutputPort가 null아 아님(3)")
    void testGetOutputPort() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("testNode");

        // When
        OutputPort outputPort = generatorNode.getOutputPort("out");

        // Then
        assertNotNull(outputPort);
    }
}
