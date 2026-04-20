package com.fbp.engine.core.node.model;

import com.fbp.engine.core.edge.connection.Connection;
import com.fbp.engine.core.edge.connection.LocalConnection;
import com.fbp.engine.core.message.Message;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.port.InputPort;
import com.fbp.engine.core.port.OutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class AbstractNodeTest {

    private static class TestNode extends AbstractNode {
        private PortMessage processedMessage;

        public TestNode(String id) {
            super(id);
        }

        private TestNode(String id, int inboxCapacity) {
            super(id, inboxCapacity);
        }

        @Override
        public void onProcess(PortMessage portMessage) {
            this.processedMessage = portMessage;
        }
    }

    private static class LimitedInboxNode extends TestNode {
        private LimitedInboxNode(String id, int inboxCapacity) {
            super(id, inboxCapacity);
        }
    }

    @Test
    @DisplayName("getId: 생성 시 지정한 ID 반환(1)")
    void testGetId() {
        // Given
        String nodeId = "testNode1";
        TestNode node = new TestNode(nodeId);

        // When
        String result = node.getId();

        // Then
        assertEquals(nodeId, result);
    }

    @Test
    @DisplayName("addInputPort: 입력 포트 추가 후 getInputPort로 조회 가능(2)")
    void testAddInputPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // When
        node.addInputPort("in");
        InputPort inputPort = node.getInputPort("in");

        // Then
        assertNotNull(inputPort);
    }

    @Test
    @DisplayName("addOutputPort: 출력 포트 추가 후 getOutputPort로 조회 가능(3)")
    void testAddOutputPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // When
        node.addOutputPort("out");
        OutputPort outputPort = node.getOutputPort("out");

        // Then
        assertNotNull(outputPort);
    }

    @Test
    @DisplayName("미등록 포트 조회 시 null 반환(4)")
    void testGetNonexistentPort() {
        // Given
        TestNode node = new TestNode("testNode1");

        // Then
        assertNull(node.getInputPort("없는포트"));
        assertNull(node.getOutputPort("없는포트"));
    }

    @Test
    @DisplayName("process: 호출 시 onProcess가 호출되는지(5)")
    void testProcessCallsOnProcess() {
        // Given
        TestNode node = new TestNode("testNode1");
        Message message = Message.of(Map.of("key", "value"));

        // When
        node.process(new PortMessage("in", message));

        // Then
        assertAll(
                () -> assertEquals("in", node.processedMessage.inputPortName()),
                () -> assertEquals(message, node.processedMessage.message())
        );
    }

    @Test
    @DisplayName("send: OutputPort로 Connection 연결 후 send() 하면 상대측에서 수신(6)")
    void testSendDeliversToConnection() {
        // Given
        TestNode node = new TestNode("testNode1");
        node.addOutputPort("out");
        Connection connection = new LocalConnection("testConnection");
        node.getOutputPort("out").connect(connection);
        Message message = Message.of(Map.of("key", "value"));

        // When
        node.send("out", message);
        Message receivedMessage = connection.take();

        // Then
        assertEquals(message, receivedMessage);
    }

    @Test
    @DisplayName("executionMode: 기본 실행 전략은 INBOX_DRIVEN인지(7)")
    void testDefaultExecutionMode() {
        // Given
        TestNode node = new TestNode("testNode1");

        // When & Then
        assertEquals(NodeExecutionMode.INBOX_DRIVEN, node.executionMode());
    }

    @Test
    @DisplayName("inbox: enqueueInput/takeInput 후 inbox size가 예상값과 일치하는지(8)")
    void testInboxSize() {
        // Given
        TestNode node = new TestNode("testNode1");
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));

        // When
        node.enqueueInput("in", message1);
        node.enqueueInput("in", message2);

        // Then
        assertEquals(2, node.getInboxSize());

        // When
        PortMessage result = node.takeInput();

        // Then
        assertAll(
                () -> assertEquals("in", result.inputPortName()),
                () -> assertEquals(message1, result.message()),
                () -> assertEquals(1, node.getInboxSize())
        );
    }

    @Test
    @DisplayName("inbox: 용량 초과 enqueueInput은 빈 공간이 생길 때까지 블로킹되는지(9)")
    void testInboxBlocksWhenFull() throws InterruptedException, ExecutionException {
        // Given
        LimitedInboxNode node = new LimitedInboxNode("testNode1", 1);
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // When
        node.enqueueInput("in", message1);
        Future<?> future = executorService.submit(() -> node.enqueueInput("in", message2));

        // Then
        try {
            assertThrows(TimeoutException.class, () -> future.get(200, TimeUnit.MILLISECONDS));

            node.takeInput();
            assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));
            assertEquals(1, node.getInboxSize());
        } finally {
            executorService.shutdownNow();
        }
    }

}
