package com.fbp.engine.protocol.modbus.node;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ModbusReaderNodeTest {

    private static class TestModbusReaderNode extends ModbusReaderNode {
        private TestModbusReaderNode(String id, Map<String, Object> config) {
            super(id, config);
        }

        private Object readConfig(String key) {
            return getConfigValue(key);
        }
    }

    @Test
    @DisplayName("포트 구성: trigger 입력 포트와 out/error 출력 포트가 존재하는지(1)")
    void testPorts() {
        // Given
        ModbusReaderNode node = new ModbusReaderNode("modbus-reader", readerConfig());

        // When & Then
        assertAll(
                () -> assertNotNull(node.getInputPort("trigger")),
                () -> assertNotNull(node.getOutputPort("out")),
                () -> assertNotNull(node.getOutputPort("error"))
        );
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 연결되지 않은 상태인지(2)")
    void testInitialState() {
        // Given
        ModbusReaderNode node = new ModbusReaderNode("modbus-reader", readerConfig());

        // When & Then
        assertFalse(node.isConnected());
    }

    @Test
    @DisplayName("config 확인: host와 slaveId 설정 값을 조회할 수 있는지(3)")
    void testConfig() {
        // Given
        TestModbusReaderNode node = new TestModbusReaderNode("modbus-reader", readerConfig());

        // When & Then
        assertAll(
                () -> assertEquals("localhost", node.readConfig("host")),
                () -> assertEquals(1, node.readConfig("slaveId"))
        );
    }

    private Map<String, Object> readerConfig() {
        return Map.of(
                "host", "localhost",
                "port", 5020,
                "slaveId", 1,
                "startAddress", 0,
                "count", 2
        );
    }
}
