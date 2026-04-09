package com.fbp.engine.node.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.node.AbstractNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterNode extends AbstractNode {
    private final String filePath;
    private BufferedWriter writer;

    public FileWriterNode(String id, String filePath) {
        super(id);
        this.filePath = filePath;
        addInputPort("in");
    }

    @Override
    public void initialize() {
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            throw new IllegalStateException("파일 초기화 중 오류가 발생했습니다: " + filePath, e);
        }
    }

    @Override
    public void onProcess(Message message) {
        if (writer == null) {
            throw new IllegalStateException("파일 writer가 초기화되지 않았습니다.");
        }

        try {
            writer.write(message.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException("파일 기록 중 오류가 발생했습니다: " + filePath, e);
        }
    }

    @Override
    public void shutdown() {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("파일 종료 중 오류가 발생했습니다: " + filePath, e);
        }
    }
}
