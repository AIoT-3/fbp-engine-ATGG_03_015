package com.fbp.engine.protocol.modbus.simulator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModbusTcpSimulatorTest {

    @Test
    @DisplayName("레지스터 초기값: setRegister 후 getRegister로 설정 값을 확인할 수 있는지(2)")
    void testSetAndGetRegister() {
        // Given
        ModbusTcpSimulator simulator = new ModbusTcpSimulator(0, 3);

        // When
        simulator.setRegister(1, 600);

        // Then
        assertEquals(600, simulator.getRegister(1));
    }
}
