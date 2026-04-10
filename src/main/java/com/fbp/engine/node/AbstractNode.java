package com.fbp.engine.node;

import com.fbp.engine.port.impl.DefaultInputPort;
import com.fbp.engine.port.impl.DefaultOutputPort;
import com.fbp.engine.port.InputPort;
import com.fbp.engine.port.OutputPort;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.exception.NodeInputDequeueException;
import com.fbp.engine.node.exception.NodeInputEnqueueException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public abstract class AbstractNode implements InboxNode {
    private final String id;
    private final Map<String, InputPort> inputPorts;
    private final Map<String, OutputPort> outputPorts;
    private final BlockingQueue<PortMessage> inbox;

    protected AbstractNode(String id) {
        this.id = id;
        this.inputPorts = new HashMap<>();
        this.outputPorts = new HashMap<>();
        this.inbox = new LinkedBlockingQueue<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(PortMessage portMessage) {
        // 시끄러워서 임시 주석 처리함
        // log.info("[{}] ==> 전처리: {}", id, message);
        onProcess(portMessage);
        // log.info("[{}] <== 후처리: {}", id, message);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public InputPort getInputPort(String name) {
        return inputPorts.get(name);
    }

    @Override
    public OutputPort getOutputPort(String name) {
        return outputPorts.get(name);
    }

    @Override
    public void enqueueInput(String inputPortName, Message message) {
        try {
            inbox.put(new PortMessage(inputPortName, message));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NodeInputEnqueueException(getId());
        }
    }

    @Override
    public PortMessage takeInput() {
        try {
            return inbox.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NodeInputDequeueException(getId());
        }
    }

    protected void addInputPort(String name) {
        inputPorts.put(name, new DefaultInputPort(name, this));
    }

    protected void addOutputPort(String name) {
        outputPorts.put(name, new DefaultOutputPort(name));
    }

    protected void send(String portName, Message message) {
        OutputPort port = outputPorts.get(portName);
        if (port != null) {
            port.send(message);
        }
    }

    public abstract void onProcess(PortMessage portMessage);
}
