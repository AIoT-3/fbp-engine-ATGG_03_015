package com.fbp.engine.node.impl;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.port.InputPort;
import com.fbp.engine.port.OutputPort;
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
class FilterNodeTest {

    @Mock
    Connection mockConnection;

    @Test
    @DisplayName("process: threshold 이상인 값을 가진 메시지가 OutputPort로 전달되는지(1)")
    void testProcessAboveThreshold() {
        // Given
        FilterNode filterNode = new FilterNode("testNode", "value", 10.0);
        filterNode.getOutputPort("out").connect(mockConnection);
        Message message = Message.of(Map.of("value", 15.0));

        // When
        filterNode.process(new PortMessage("in", message));

        // Then
        then(mockConnection).should().deliver(message);
    }

    @Test
    @DisplayName("process: threshold 미만인 값을 가진 메시지가 OutputPort로 전달되지 않는지(2)")
    void testProcessBelowThreshold() {
        // Given
        FilterNode filterNode = new FilterNode("testNode", "value", 10.0);
        filterNode.getOutputPort("out").connect(mockConnection);
        Message message = Message.of(Map.of("value", 5.0));

        // When
        filterNode.process(new PortMessage("in", message));

        // Then
        then(mockConnection).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("process: threshold과 동일한 값을 가진 메시지가 OutputPort로 전달되는지(3)")
    void testProcessAtThreshold() {
        // Given
        FilterNode filterNode = new FilterNode("testNode", "value", 10.0);
        filterNode.getOutputPort("out").connect(mockConnection);
        Message message = Message.of(Map.of("value", 10.0));

        // When
        filterNode.process(new PortMessage("in", message));

        // Then
        then(mockConnection).should().deliver(message);
    }

    @Test
    @DisplayName("process: 필터링 대상 키가 없는 메시지가 들어왔을 때 예외 없이 처리되는지(4)")
    void testProcessMissingKey() {
        // Given
        FilterNode filterNode = new FilterNode("testNode", "value", 10.0);
        filterNode.getOutputPort("out").connect(mockConnection);
        Message message = Message.of(Map.of("otherKey", 15.0));

        // When & Then
        assertDoesNotThrow(() -> filterNode.process(new PortMessage("in", message)));
        then(mockConnection).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("getInputPort(in), getOutputPort(out)가 null이 아님(3)")
    void testGetPorts() {
        // Given
        FilterNode filterNode = new FilterNode("testNode", "value", 10.0);

        // When
        InputPort inputPort = filterNode.getInputPort("in");
        OutputPort outputPort = filterNode.getOutputPort("out");

        // Then
        assertAll(
                () -> assertNotNull(inputPort),
                () -> assertNotNull(outputPort)
        );
    }
}
