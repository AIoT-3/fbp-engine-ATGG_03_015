package com.fbp.engine.protocol.modbus.client;

import com.fbp.engine.protocol.modbus.frame.ModbusFrameEncoder;
import com.fbp.engine.protocol.modbus.frame.request.ModbusRequest;
import com.fbp.engine.protocol.modbus.frame.request.ReadHoldingRegistersRequestPdu;
import com.fbp.engine.protocol.modbus.frame.request.WriteSingleRegisterRequestPdu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ModbusTcpClientTest {

    @Test
    @DisplayName("FC 03 요청 프레임 조립: unitId, startAddress, quantity가 올바른 위치에 조립되는지(1)")
    void testFc03RequestFrame() {
        // Given
        ModbusRequest request = ModbusRequest.of(
                1,
                1,
                new ReadHoldingRegistersRequestPdu(10, 5)
        );

        // When
        byte[] frame = ModbusFrameEncoder.encode(request);

        // Then
        assertArrayEquals(new byte[]{
                0x00, 0x01,
                0x00, 0x00,
                0x00, 0x06,
                0x01,
                0x03,
                0x00, 0x0A,
                0x00, 0x05
        }, frame);
    }

    @Test
    @DisplayName("FC 06 요청 프레임 조립: unitId, address, value가 올바른 위치에 조립되는지(2)")
    void testFc06RequestFrame() {
        // Given
        ModbusRequest request = ModbusRequest.of(
                1,
                1,
                new WriteSingleRegisterRequestPdu(5, 1234)
        );

        // When
        byte[] frame = ModbusFrameEncoder.encode(request);

        // Then
        assertArrayEquals(new byte[]{
                0x00, 0x01,
                0x00, 0x00,
                0x00, 0x06,
                0x01,
                0x06,
                0x00, 0x05,
                0x04, (byte) 0xD2
        }, frame);
    }

    @Test
    @DisplayName("MBAP 헤더 구조: Transaction ID, Protocol ID, Length, Unit ID가 올바르게 조립되는지(3)")
    void testMbapHeader() {
        // Given
        ModbusRequest request = ModbusRequest.of(
                7,
                1,
                new ReadHoldingRegistersRequestPdu(0, 3)
        );

        // When
        byte[] frame = ModbusFrameEncoder.encode(request);

        // Then
        assertAll(
                () -> assertEquals(0x0007, unsignedShort(frame, 0)),
                () -> assertEquals(0x0000, unsignedShort(frame, 2)),
                () -> assertEquals(0x0006, unsignedShort(frame, 4)),
                () -> assertEquals(0x01, frame[6] & 0xFF)
        );
    }

    @Test
    @DisplayName("Transaction ID 증가: 연속 요청 시 Transaction ID가 1씩 증가하는지(4)")
    void testTransactionIdIncrements() throws Exception {
        // Given
        ModbusTcpClient client = new ModbusTcpClient("localhost", 5020);
        Method nextTransactionId = ModbusTcpClient.class.getDeclaredMethod("nextTransactionId");
        nextTransactionId.setAccessible(true);

        // When & Then
        assertAll(
                () -> assertEquals(1, nextTransactionId.invoke(client)),
                () -> assertEquals(2, nextTransactionId.invoke(client))
        );
    }

    @Test
    @DisplayName("초기 상태: 생성 직후 연결되지 않은 상태인지(5)")
    void testInitialState() {
        // Given
        ModbusTcpClient client = new ModbusTcpClient("localhost", 5020);

        // When & Then
        assertFalse(client.isConnected());
    }

    private int unsignedShort(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }
}
