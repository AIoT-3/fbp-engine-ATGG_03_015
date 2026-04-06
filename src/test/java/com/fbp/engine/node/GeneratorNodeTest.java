package com.fbp.engine.node;

import com.fbp.engine.core.OutputPort;
import com.fbp.engine.message.Message;
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
    private OutputPort mockOutputPort;

    @Test
    @DisplayName("generate: OutputPort의 send()가 호출되고, 메시지에 key와 value가 포함되는지(1)(2)")
    void testGenerate() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("testNode");
        generatorNode.setOutputPort(mockOutputPort);
        String testKey = "testKey";
        String testValue = "testValue";

        // When
        generatorNode.generate("testKey", "testValue");

        // Then
        then(mockOutputPort).should().send(any(Message.class));
        then(mockOutputPort).should().send(argThat(message ->
                "testValue".equals(message.get(testKey))
        ));
    }

    @Test
    @DisplayName("generate: 3번 호출 시 3개의 메시지가 순차적으로 OutputPort로 전달되는지(4)")
    void testGenerateMultipleTimes() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("testNode");
        generatorNode.setOutputPort(mockOutputPort);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);

        // When
        generatorNode.generate("key1", "value1");
        generatorNode.generate("key2", "value2");
        generatorNode.generate("key3", "value3");

        // Then
        then(mockOutputPort).should(times(3)).send(messageCaptor.capture());
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
        generatorNode.setOutputPort(mockOutputPort);

        // When
        OutputPort outputPort = generatorNode.getOutputPort();

        // Then
        assertNotNull(outputPort);
        assertEquals(mockOutputPort, outputPort);
    }
}
