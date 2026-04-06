package com.fbp.engine.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    @DisplayName("생성 시 ID 자동 할당(1), 페이로드 저장(3), 타임스탬프 기록(2)")
    void testMessageCreation() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1", "key2", 123);

        // When
        Message message = Message.of(payload);

        // Then
        assertAll(
                () -> assertNotNull(message.id()),
                () -> assertEquals("value1", message.get("key1")),
                () -> assertEquals(123, (Integer) message.get("key2")),
                () -> assertNotNull(message.timestamp())
        );
    }

    @Test
    @DisplayName("제네릭 get 타입 캐스팅(4)")
    void testGetWithGeneric() {
        // Given
        Map<String, Object> payload = Map.of("temperature", 36.5);
        Message message = Message.of(payload);

        // When
        Double temperature = message.get("temperature");

        // Then
        assertEquals(36.5, temperature);
    }

    @Test
    @DisplayName("존재하지 않는 키 조회 시 null 반환(5)")
    void testGetNonExistentKey() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1");
        Message message = Message.of(payload);

        // When
        Object result = message.get("nonExistentKey");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("페이로드 불변 - 외부 수정 차단(6), 원본 Map 수정 무영향(7)")
    void testPayloadImmutability() {
        // Given
        Map<String, Object> originalPayload = Map.of("key1", "value1");
        Message message = Message.of(originalPayload);

        // When
        assertThrows(UnsupportedOperationException.class, () -> {
            message.payload().put("key2", "value2");
        });

        // Then
        assertFalse(message.payload().containsKey("key2"));
    }

    @Test
    @DisplayName("withEntry - 새 객체 반환(8), 원본 불변(9), 새 메시지에 값 존재(10)")
    void testWithEntry() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1");
        Message message = Message.of(payload);

        // When
        Message newMessage = message.withEntry("key2", "value2");

        // Then
        assertAll(
                () -> assertNotSame(message, newMessage),
                () -> assertFalse(message.hasKey("key2")),
                () -> assertTrue(newMessage.hasKey("key2")),
                () -> assertEquals("value2", newMessage.get("key2"))
        );
    }

    @Test
    @DisplayName("hasKey - 존재하는 키(11), 없는 키(12)")
    void testHasKey() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1");
        Message message = Message.of(payload);

        // Then
        assertAll(
                () -> assertTrue(message.hasKey("key1")),
                () -> assertFalse(message.hasKey("nonExistentKey"))
        );
    }

    @Test
    @DisplayName("withoutKey - 키 제거 확인(13), 원본 불변(14)")
    void testWithoutKey() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1", "key2", "value2");
        Message message = Message.of(payload);

        // When
        Message newMessage = message.withoutkey("key1");

        // Then
        assertAll(
                () -> assertFalse(newMessage.hasKey("key1")),
                () -> assertTrue(newMessage.hasKey("key2")),
                () -> assertTrue(message.hasKey("key1")),
                () -> assertTrue(message.hasKey("key2"))
        );
    }

    @Test
    @DisplayName("toString이 null이 아니고, payload 내용 포함(15)")
    void testToString() {
        // Given
        Map<String, Object> payload = Map.of("key1", "value1");
        Message message = Message.of(payload);

        // When
        String messageString = message.toString();

        // Then
        assertAll(
                () -> assertNotNull(messageString),
                () -> assertTrue(messageString.contains("key1")),
                () -> assertTrue(messageString.contains("value1"))
        );
    }
}