package com.fbp.engine.protocol.modbus.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ModbusWriterNodeTest {

    private static class TestModbusWriterNode extends ModbusWriterNode {
        private TestModbusWriterNode(String id, Map<String, Object> config) {
            super(id, config);
        }

        private Object readConfig(String key) {
            return getConfigValue(key);
        }
    }

    @Test
    @DisplayName("포트 구성: in 입력 포트가 존재하는지(1)")
    void testPorts() {
        // Given
        ModbusWriterNode node = new ModbusWriterNode("modbus-writer", writerConfig());

        // When & Then
        assertNotNull(node.getInputPort("in"));
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 연결되지 않은 상태인지(2)")
    void testInitialState() {
        // Given
        ModbusWriterNode node = new ModbusWriterNode("modbus-writer", writerConfig());

        // When & Then
        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("config 확인: registerAddress 설정 값을 조회할 수 있는지(3)")
    void testConfigRegisterAddress() {
        // Given
        TestModbusWriterNode node = new TestModbusWriterNode("modbus-writer", writerConfig());

        // When & Then
        assertEquals(2, node.readConfig("registerAddress"));
    }

    private Map<String, Object> writerConfig() {
        return Map.of(
                "host", "localhost",
                "port", 5020,
                "slaveId", 1,
                "registerAddress", 2,
                "valueField", "value"
        );
    }
}
