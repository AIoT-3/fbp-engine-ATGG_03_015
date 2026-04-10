package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.builtin.sink.CollectorNode;
import com.fbp.engine.core.node.builtin.source.GeneratorNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollectorNodeTest {

    @Test
    @DisplayName("onProcess: 메시지 수집과 순서가 보존되는지(1)(2)")
    void testOnProcess() {
        // Given
        CollectorNode collectorNode = new CollectorNode("collector");
        Message message1 = Message.of(Map.of("value", 1));
        Message message2 = Message.of(Map.of("value", 2));
        Message message3 = Message.of(Map.of("value", 3));

        // When
        collectorNode.process(new PortMessage("in", message1));
        collectorNode.process(new PortMessage("in", message2));
        collectorNode.process(new PortMessage("in", message3));

        // Then
        assertAll(
                () -> assertEquals(3, collectorNode.getCollected().size()),
                () -> assertEquals(message1, collectorNode.getCollected().get(0)),
                () -> assertEquals(message2, collectorNode.getCollected().get(1)),
                () -> assertEquals(message3, collectorNode.getCollected().get(2))
        );
    }

    @Test
    @DisplayName("초기 상태: 빈 리스트와 InputPort가 존재하는지(3)(4)")
    void testInitialState() {
        // Given
        CollectorNode collectorNode = new CollectorNode("collector");

        // Then
        assertAll(
                () -> assertTrue(collectorNode.getCollected().isEmpty()),
                () -> assertNotNull(collectorNode.getInputPort("in"))
        );
    }

    @Test
    @DisplayName("파이프라인: Generator가 보낸 메시지가 Collector에 수집되는지(5)")
    void testPipeline() {
        // Given
        GeneratorNode generatorNode = new GeneratorNode("generator");
        CollectorNode collectorNode = new CollectorNode("collector");
        Connection connection = new LocalConnection("generator-to-collector");
        generatorNode.getOutputPort("out").connect(connection);

        // When
        generatorNode.generate("value", 1);
        generatorNode.generate("value", 2);
        generatorNode.generate("value", 3);
        collectorNode.getInputPort("in").receive(connection.poll());
        collectorNode.getInputPort("in").receive(connection.poll());
        collectorNode.getInputPort("in").receive(connection.poll());
        collectorNode.process(collectorNode.takeInput());
        collectorNode.process(collectorNode.takeInput());
        collectorNode.process(collectorNode.takeInput());

        // Then
        assertAll(
                () -> assertEquals(3, collectorNode.getCollected().size()),
                () -> assertEquals((Integer) 1, collectorNode.getCollected().get(0).get("value")),
                () -> assertEquals((Integer) 2, collectorNode.getCollected().get(1).get("value")),
                () -> assertEquals((Integer) 3, collectorNode.getCollected().get(2).get("value"))
        );
    }
}
