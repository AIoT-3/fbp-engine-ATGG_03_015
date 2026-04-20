package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ThresholdFilterNodeTest {

    @Test
    @DisplayName("onProcess: 초과는 alert, 이하는 normal로 전달되는지(1)(2)(5)")
    void testOnProcess_AlertAndNormal() {
        // Given
        ThresholdFilterNode thresholdFilterNode = new ThresholdFilterNode("filter", "temperature", 30);
        Connection alertConnection = new LocalConnection("alert-connection");
        Connection normalConnection = new LocalConnection("normal-connection");
        thresholdFilterNode.getOutputPort("alert").connect(alertConnection);
        thresholdFilterNode.getOutputPort("normal").connect(normalConnection);

        Message alertMessage = Message.of(Map.of("temperature", 31.0));
        Message normalMessage = Message.of(Map.of("temperature", 29.0));

        // When
        thresholdFilterNode.process(new PortMessage("in", alertMessage));
        thresholdFilterNode.process(new PortMessage("in", normalMessage));
        Message alertResult = alertConnection.take();
        Message normalResult = normalConnection.take();

        // Then
        assertAll(
                () -> assertEquals(31.0, alertResult.get("temperature")),
                () -> assertEquals(29.0, normalResult.get("temperature"))
        );
    }

    @Test
    @DisplayName("onProcess: 경계값은 normal로 전달되는지(3)")
    void testOnProcess_ThresholdBoundary() {
        // Given
        ThresholdFilterNode thresholdFilterNode = new ThresholdFilterNode("filter", "temperature", 30);
        Connection alertConnection = new LocalConnection("alert-connection");
        Connection normalConnection = new LocalConnection("normal-connection");
        thresholdFilterNode.getOutputPort("alert").connect(alertConnection);
        thresholdFilterNode.getOutputPort("normal").connect(normalConnection);

        // When
        thresholdFilterNode.process(new PortMessage("in", Message.of(Map.of("temperature", 30.0))));

        // Then
        assertAll(
                () -> assertEquals(0, alertConnection.getBufferSize()),
                () -> assertEquals(1, normalConnection.getBufferSize())
        );
    }

    @Test
    @DisplayName("onProcess: 키 없는 메시지는 예외 없이 무시되는지(4)")
    void testOnProcess_MissingKey() {
        // Given
        ThresholdFilterNode thresholdFilterNode = new ThresholdFilterNode("filter", "temperature", 30);
        Connection alertConnection = new LocalConnection("alert-connection");
        Connection normalConnection = new LocalConnection("normal-connection");
        thresholdFilterNode.getOutputPort("alert").connect(alertConnection);
        thresholdFilterNode.getOutputPort("normal").connect(normalConnection);

        // When & Then
        assertDoesNotThrow(() -> thresholdFilterNode.process(new PortMessage("in", Message.of(Map.of("humidity", 50.0)))));
        assertAll(
                () -> assertEquals(0, alertConnection.getBufferSize()),
                () -> assertEquals(0, normalConnection.getBufferSize())
        );
    }
}
