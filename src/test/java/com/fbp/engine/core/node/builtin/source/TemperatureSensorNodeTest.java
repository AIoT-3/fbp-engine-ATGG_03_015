package com.fbp.engine.core.node.builtin.source;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureSensorNodeTest {

    @Test
    @DisplayName("onProcess: 온도 범위, 필수 키, sensorId가 올바른지(1)(2)(3)")
    void testOnProcess() {
        // Given
        TemperatureSensorNode temperatureSensorNode = new TemperatureSensorNode("tempSensor", 15, 45);
        Connection connection = new LocalConnection("temp-to-next");
        temperatureSensorNode.getOutputPort("out").connect(connection);

        // When
        temperatureSensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        Message result = connection.take();
        Double temperature = result.get("temperature");

        // Then
        assertAll(
                () -> assertNotNull(temperature),
                () -> assertTrue(temperature >= 15.0 && temperature <= 45.0),
                () -> assertEquals("tempSensor", result.get("sensorId")),
                () -> assertEquals("°C", result.get("unit")),
                () -> assertNotNull(result.get("timestamp"))
        );
    }

    @Test
    @DisplayName("onProcess: 트리거마다 출력 메시지가 생성되는지(4)")
    void testOnProcess_MultipleTriggers() {
        // Given
        TemperatureSensorNode temperatureSensorNode = new TemperatureSensorNode("tempSensor", 15, 45);
        Connection connection = new LocalConnection("temp-to-next");
        temperatureSensorNode.getOutputPort("out").connect(connection);

        // When
        temperatureSensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        temperatureSensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        temperatureSensorNode.process(new PortMessage("trigger", Message.of(Map.of())));

        // Then
        assertEquals(3, connection.getBufferSize());
    }
}
