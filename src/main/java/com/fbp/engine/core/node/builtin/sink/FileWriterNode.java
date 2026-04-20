package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;

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
            throw new EngineException(EngineFailureType.FILE_NODE_OPERATION_FAILED, e, filePath);
        }
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        if (writer == null) {
            throw new EngineException(EngineFailureType.FILE_NODE_NOT_INITIALIZED);
        }

        try {
            writer.write(portMessage.message().toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new EngineException(EngineFailureType.FILE_NODE_OPERATION_FAILED, e, filePath);
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
            throw new EngineException(EngineFailureType.FILE_NODE_OPERATION_FAILED, e, filePath);
        }
    }
}
