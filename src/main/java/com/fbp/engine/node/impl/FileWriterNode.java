package com.fbp.engine.node.impl;

import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import com.fbp.engine.node.exception.FileNodeOperationException;

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
            throw new FileNodeOperationException(String.format("파일 초기화 오류(%s)", filePath), e);
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        if (writer == null) {
            throw new FileNodeOperationException();
        }

        try {
            writer.write(portMessage.message().toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new FileNodeOperationException(String.format("파일 기록 중 오류(%s)", filePath), e);
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
            throw new FileNodeOperationException(String.format("파일 종료 중 오류(%s)", filePath), e);
        }
    }
}
