package com.fbp.engine.core.node.protocol;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProtocolNodeTest {

    private static class TestProtocolNode extends ProtocolNode {
        private final int successAttempt;
        private int connectAttempts;
        private boolean disconnected;

        private TestProtocolNode(Map<String, Object> config, long retryIntervalMs, int successAttempt) {
            super("test-protocol", config, retryIntervalMs);
            this.successAttempt = successAttempt;
        }

        @Override
        protected void doConnect() {
            connectAttempts++;
            if (connectAttempts < successAttempt) {
                throw new EngineException(EngineFailureType.PROTOCOL_CONNECTION_FAILED);
            }
        }

        @Override
        protected void doDisconnect() {
            disconnected = true;
        }

        @Override
        public void onProcess(PortMessage portMessage) {
        }
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 DISCONNECTED인지(1)")
    void testInitialState() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of(), 1, 1);

        // When & Then
        assertAll(
                () -> assertEquals(ProtocolConnectionState.DISCONNECTED, node.getConnectionState()),
                () -> assertFalse(node.isConnected())
        );
    }

    @Test
    @DisplayName("config 조회: 생성 시 전달한 config 값을 조회할 수 있는지(2)")
    void testGetConfigValue() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("host", "localhost", "port", 9090), 1, 1);

        // When & Then
        assertAll(
                () -> assertEquals("localhost", node.getConfigValue("host")),
                () -> assertEquals(9090, node.getConfigValue("port"))
        );
    }

    @Test
    @DisplayName("initialize: 연결 성공 시 CONNECTED로 변경되는지(3)")
    void testInitializeConnected() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("retryCount", 1), 1, 1);

        // When
        node.initialize();

        // Then
        assertAll(
                () -> assertEquals(ProtocolConnectionState.CONNECTED, node.getConnectionState()),
                () -> assertTrue(node.isConnected()),
                () -> assertEquals(1, node.connectAttempts)
        );
    }

    @Test
    @DisplayName("initialize: 연결 실패 시 ERROR 상태로 남고 예외가 전달되는지(4)")
    void testInitializeConnectionFailure() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("retryCount", 2), 1, 3);

        // When
        EngineException exception = assertThrows(EngineException.class, node::initialize);

        // Then
        assertAll(
                () -> assertEquals(EngineFailureType.PROTOCOL_CONNECTION_FAILED, exception.getFailureType()),
                () -> assertEquals(ProtocolConnectionState.ERROR, node.getConnectionState()),
                () -> assertFalse(node.isConnected()),
                () -> assertEquals(2, node.connectAttempts)
        );
    }

    @Test
    @DisplayName("shutdown: 연결 해제 후 DISCONNECTED로 변경되는지(5)")
    void testShutdownDisconnected() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("retryCount", 1), 1, 1);
        node.initialize();

        // When
        node.shutdown();

        // Then
        assertAll(
                () -> assertTrue(node.disconnected),
                () -> assertEquals(ProtocolConnectionState.DISCONNECTED, node.getConnectionState()),
                () -> assertFalse(node.isConnected())
        );
    }

    @Test
    @DisplayName("isConnected: CONNECTED 상태에서만 true를 반환하는지(6)")
    void testIsConnected() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("retryCount", 1), 1, 1);

        // When & Then
        assertFalse(node.isConnected());

        // When
        node.initialize();

        // Then
        assertTrue(node.isConnected());

        // When
        node.shutdown();

        // Then
        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("재연결 시도: 실패 후 retryCount 범위 안에서 재시도하는지(7)")
    void testRetryUntilConnected() {
        // Given
        TestProtocolNode node = new TestProtocolNode(Map.of("retryCount", 3), 1, 3);

        // When
        node.initialize();

        // Then
        assertAll(
                () -> assertEquals(ProtocolConnectionState.CONNECTED, node.getConnectionState()),
                () -> assertTrue(node.isConnected()),
                () -> assertEquals(3, node.connectAttempts)
        );
    }
}
