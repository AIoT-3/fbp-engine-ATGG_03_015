package com.fbp.engine.assignment;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class A1S402 {
    private static final BlockingQueue<String> buffer = new LinkedBlockingQueue<>();

    public static void main(String[] args) {

        // 생산자 스레드 (0.1초마다 "메시지-0" ~ "메시지-99"를 buffer.add())
        Thread producerThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String message = "메시지-" + i;
                try {
                    buffer.put(message);
                    log.info("생산자: {} (버퍼 크기: {})", message, buffer.size());
                    Thread.sleep(100); // 0.1초 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        // 소비자 스레드 (while 루프에서 buffer.isEmpty()가 아니면 buffer.remove(0)로 출력)
        Thread consumerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String message = null;
                try {
                    message = buffer.take();
                    log.info("소비자: {}", message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.interrupt();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
