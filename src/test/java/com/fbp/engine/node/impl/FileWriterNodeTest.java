package com.fbp.engine.node.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.exception.FileNodeOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileWriterNodeTest {

    @Test
    @DisplayName("initialize: 지정 경로에 파일이 생성되는지(1)")
    void testInitialize() throws IOException {
        // Given
        Path tempFile = Files.createTempFile("file-writer-node", ".txt");
        Files.deleteIfExists(tempFile);
        FileWriterNode fileWriterNode = new FileWriterNode("fileWriter", tempFile.toString());

        // When
        fileWriterNode.initialize();
        fileWriterNode.shutdown();

        // Then
        assertTrue(Files.exists(tempFile));
    }

    @Test
    @DisplayName("onProcess/shutdown: 메시지 3개를 파일에 기록하는지(2)")
    void testOnProcess() throws IOException {
        // Given
        Path tempFile = Files.createTempFile("file-writer-node", ".txt");
        FileWriterNode fileWriterNode = new FileWriterNode("fileWriter", tempFile.toString());
        fileWriterNode.initialize();

        // When
        fileWriterNode.process(new PortMessage("in", Message.of(Map.of("value", 1))));
        fileWriterNode.process(new PortMessage("in", Message.of(Map.of("value", 2))));
        fileWriterNode.process(new PortMessage("in", Message.of(Map.of("value", 3))));
        fileWriterNode.shutdown();

        List<String> lines = Files.readAllLines(tempFile);

        // Then
        assertEquals(3, lines.size());
    }

    @Test
    @DisplayName("shutdown: 종료 후 추가 기록 시 예외가 발생하는지(3)")
    void testShutdown() throws IOException {
        // Given
        Path tempFile = Files.createTempFile("file-writer-node", ".txt");
        FileWriterNode fileWriterNode = new FileWriterNode("fileWriter", tempFile.toString());
        fileWriterNode.initialize();
        fileWriterNode.shutdown();

        // When & Then
        assertThrows(FileNodeOperationException.class,
                () -> fileWriterNode.process(new PortMessage("in", Message.of(Map.of("value", 1)))));
    }
}
