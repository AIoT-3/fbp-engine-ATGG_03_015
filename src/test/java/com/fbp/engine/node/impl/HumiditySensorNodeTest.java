package com.fbp.engine.node.impl;

import com.fbp.engine.edge.connection.Connection;
import com.fbp.engine.edge.connection.LocalConnection;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HumiditySensorNodeTest {

    @Test
    @DisplayName("onProcess: 습도 범위, 필수 키, sensorId가 올바른지(1)(2)(3)")
    void testOnProcess() {
        // Given
        HumiditySensorNode humiditySensorNode = new HumiditySensorNode("humiditySensor", 30, 90);
        Connection connection = new LocalConnection("humidity-to-next");
        humiditySensorNode.getOutputPort("out").connect(connection);

        // When
        humiditySensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        Message result = connection.poll();
        Double humidity = result.get("humidity");

        // Then
        assertAll(
                () -> assertNotNull(humidity),
                () -> assertTrue(humidity >= 30.0 && humidity <= 90.0),
                () -> assertEquals("humiditySensor", result.get("sensorId")),
                () -> assertEquals("%", result.get("unit")),
                () -> assertNotNull(result.get("timestamp"))
        );
    }

    @Test
    @DisplayName("onProcess: 트리거마다 출력 메시지가 생성되는지(4)")
    void testOnProcess_MultipleTriggers() {
        // Given
        HumiditySensorNode humiditySensorNode = new HumiditySensorNode("humiditySensor", 30, 90);
        Connection connection = new LocalConnection("humidity-to-next");
        humiditySensorNode.getOutputPort("out").connect(connection);

        // When
        humiditySensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        humiditySensorNode.process(new PortMessage("trigger", Message.of(Map.of())));
        humiditySensorNode.process(new PortMessage("trigger", Message.of(Map.of())));

        // Then
        assertEquals(3, connection.getBufferSize());
    }
}
