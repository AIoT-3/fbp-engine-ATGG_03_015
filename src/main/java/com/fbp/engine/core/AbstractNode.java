package com.fbp.engine.core;

import com.fbp.engine.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractNode implements Node {
    private final String id;
    private final Map<String, InputPort> inputPorts;
    private final Map<String, OutputPort> outputPorts;

    protected AbstractNode(String id) {
        this.id = id;
        this.inputPorts = new HashMap<>();
        this.outputPorts = new HashMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void process(Message message) {
        // 시끄러워서 주석 처리함
        // log.info("[{}] ==> 전처리: {}", id, message);
        onProcess(message);
        // log.info("[{}] <== 후처리: {}", id, message);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

    protected void addInputPort(String name) {
        inputPorts.put(name, new DefaultInputPort(name, this));
    }

    protected void addOutputPort(String name) {
        outputPorts.put(name, new DefaultOutputPort(name));
    }

    public InputPort getInputPort(String name) {
        return inputPorts.get(name);
    }

    public OutputPort getOutputPort(String name) {
        return outputPorts.get(name);
    }

    protected void send(String portName, Message message) {
        OutputPort port = outputPorts.get(portName);
        if (port != null) {
            port.send(message);
        }
    }

    public abstract void onProcess(Message message);
}
