package com.fbp.engine.core.node.builtin.source;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.node.NodeExecutionMode;
import com.fbp.engine.core.node.builtin.source.TimerNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimerNodeTest {

    @Test
    @DisplayName("executionMode: TimerNode는 SELF_DRIVEN인지(0)")
    void testExecutionMode() {
        // Given
        TimerNode timerNode = new TimerNode("timerNode1", 100);

        // When & Then
        assertEquals(NodeExecutionMode.SELF_DRIVEN, timerNode.executionMode());
    }

    @Test
    @DisplayName("initialize: 호출 후 일정 시간 대기하면 메시지가 OutputPort로 전달되는지(1)")
    void testInitialize() {
        // Given
        TimerNode timerNode = new TimerNode("timerNode1", 100);
        Connection connection = new LocalConnection("testConnection");
        timerNode.getOutputPort("out").connect(connection);

        // When
        timerNode.initialize();
        Message message = connection.poll();
        timerNode.shutdown();

        // Then
        assertNotNull(message);
    }

    @Test
    @DisplayName("tick 증가: 수신한 메시지들의 tick이 0부터 1씩 증가하는지(2)")
    void testTickIncrement() {
        // Given
        TimerNode timerNode = new TimerNode("timerNode1", 10);
        Connection connection = new LocalConnection("testConnection");
        timerNode.getOutputPort("out").connect(connection);

        // When
        timerNode.initialize();
        Message message1 = connection.poll();
        Message message2 = connection.poll();
        Message message3 = connection.poll();
        Message message4 = connection.poll();
        timerNode.shutdown();

        // Then
        assertAll(
                () -> assertNotNull(message1),
                () -> assertNotNull(message2),
                () -> assertNotNull(message3),
                () -> assertNotNull(message4),
                () -> assertEquals(0, (Integer) message1.get("tick")),
                () -> assertEquals(1, (Integer) message2.get("tick")),
                () -> assertEquals(2, (Integer) message3.get("tick")),
                () -> assertEquals(3, (Integer) message4.get("tick"))
        );
    }

    @Test
    @DisplayName("shutdown: 호출 후 메시지 전송이 중단되는지(3)")
    void testShutdown() {
        // Given
        TimerNode timerNode = new TimerNode("timerNode1", 10);
        Connection connection = new LocalConnection("testConnection");
        timerNode.getOutputPort("out").connect(connection);

        // When
        timerNode.initialize();
        Message message = connection.poll();
        timerNode.shutdown();
        int sizeAfterShutdown = connection.getBufferSize();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        assertAll(
                () -> assertNotNull(message),
                () -> assertEquals(sizeAfterShutdown, connection.getBufferSize())
        );
    }

    @Test
    @DisplayName("주기 확인: 500ms 설정 시 2초간 대략 4개의 메시지가 전달되는지(4)")
    void testInterval() {
        // Given
        TimerNode timerNode = new TimerNode("timerNode1", 500);
        Connection connection = new LocalConnection("testConnection");
        timerNode.getOutputPort("out").connect(connection);

        // When
        timerNode.initialize();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        timerNode.shutdown();
        int messageCount = connection.getBufferSize();

        // Then
        assertTrue(messageCount >= 3 && messageCount <= 5);
    }
}
