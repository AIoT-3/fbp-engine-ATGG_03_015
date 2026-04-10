package com.fbp.engine.edge;

import com.fbp.engine.message.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class LocalConnectionTest {

    @Test
    @DisplayName("deliver-poll 기본 동작: deliver()한 메시지를 poll()로 꺼낼 수 있고, getBufferSize()가 예상값과 일치하는지(1)(6)")
    void testDeliverAndPoll() {
        // Given
        Connection connection = new LocalConnection("testConnection");
        Message message = Message.of(Map.of("key", "value"));

        // When
        connection.deliver(message);
        Message result = connection.poll();

        // Then
        assertAll(
                () -> assertEquals(message, result),
                () -> assertEquals(0, connection.getBufferSize())
        );
    }

    @Test
    @DisplayName("메시지 순서 보장: deliver()한 여러 메시지가 poll()에서 FIFO 순서대로 꺼내지는지(2)")
    void testPollMultipleMessagesInOrder() {
        // Given
        Connection connection = new LocalConnection("testConnection");
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));
        Message message3 = Message.of(Map.of("key3", "value3"));

        // When
        connection.deliver(message1);
        connection.deliver(message2);
        connection.deliver(message3);

        List<Message> results = List.of(
                connection.poll(),
                connection.poll(),
                connection.poll()
        );

        // Then
        assertAll(
                () -> assertEquals(List.of(message1, message2, message3), results),
                () -> assertEquals(0, connection.getBufferSize())
        );
    }

    @Test
    @DisplayName("멀티스레드 deliver-poll: 별도 스레드에서 deliver()하고 다른 스레드에서 poll()하여 수신 성공하는지(3)")
    void testDeliverAndPollWithMultipleThreads() throws InterruptedException {
        // Given
        Connection connection = new LocalConnection("testConnection");
        Message message = Message.of(Map.of("key", "value"));
        CountDownLatch receivedLatch = new CountDownLatch(1);
        Message[] received = new Message[1];

        Thread consumerThread = new Thread(() -> {
            received[0] = connection.poll();
            receivedLatch.countDown();
        });

        // When
        consumerThread.start();
        connection.deliver(message);

        // Then
        assertTrue(receivedLatch.await(1, TimeUnit.SECONDS));
        assertEquals(message, received[0]);
    }

    @Test
    @DisplayName("poll 대기: deliver() 전에 poll() 호출 시 메시지 도착까지 블로킹되는지(4)")
    void testPollBlocksUntilMessageArrives() {
        // Given
        Connection connection = new LocalConnection("testConnection");
        Message message = Message.of(Map.of("key", "value"));

        // When & Then
        assertTimeout(Duration.ofSeconds(1), () -> {
            CompletableFuture<Message> future = CompletableFuture.supplyAsync(connection::poll);

            Thread.sleep(200);
            assertFalse(future.isDone());

            connection.deliver(message);
            assertEquals(message, future.get(500, TimeUnit.MILLISECONDS));
        });
    }

    @Test
    @DisplayName("버퍼 크기 제한: 크기 2인 Connection에 3번째 deliver() 시 블로킹되는지(5)")
    void testDeliverBlocksWhenBufferIsFull() throws InterruptedException, ExecutionException {
        // Given
        Connection connection = new LocalConnection("testConnection", 2);
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));
        Message message3 = Message.of(Map.of("key3", "value3"));
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // When
        connection.deliver(message1);
        connection.deliver(message2);
        Future<?> future = executorService.submit(() -> connection.deliver(message3));

        // Then
        try {
            assertThrows(TimeoutException.class, () -> future.get(200, TimeUnit.MILLISECONDS));

            connection.poll();
            assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));
            assertEquals(2, connection.getBufferSize());
        } finally {
            executorService.shutdownNow();
        }
    }

    @Test
    @DisplayName("버퍼 크기 조회: 여러 메시지 deliver()와 poll() 이후 getBufferSize()가 예상값과 일치하는지(6)")
    void testGetBufferSize() {
        // Given
        Connection connection = new LocalConnection("testConnection");
        Message message1 = Message.of(Map.of("key1", "value1"));
        Message message2 = Message.of(Map.of("key2", "value2"));

        // When
        connection.deliver(message1);
        connection.deliver(message2);

        // Then
        assertEquals(2, connection.getBufferSize());

        // When
        connection.poll();

        // Then
        assertEquals(1, connection.getBufferSize());
    }
}
