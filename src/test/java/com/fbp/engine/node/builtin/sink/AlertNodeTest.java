package com.fbp.engine.node.builtin.sink;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AlertNodeTest {

    @Test
    @DisplayName("onProcess: 정상 메시지와 키 누락 메시지를 예외 없이 처리하는지(1)(2)")
    void testOnProcess() {
        // Given
        AlertNode alertNode = new AlertNode("alert");
        Message validMessage = Message.of(Map.of("sensorId", "tempSensor", "temperature", 31.5));
        Message invalidMessage = Message.of(Map.of("sensorId", "tempSensor"));

        // When & Then
        assertDoesNotThrow(() -> {
            alertNode.process(new PortMessage("in", validMessage));
            alertNode.process(new PortMessage("in", invalidMessage));
        });
    }
}
