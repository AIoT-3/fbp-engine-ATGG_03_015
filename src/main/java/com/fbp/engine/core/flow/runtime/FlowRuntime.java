package com.fbp.engine.core.flow.runtime;

import com.fbp.engine.core.flow.exception.FlowRuntimeException;
import com.fbp.engine.core.edge.Edge;
import com.fbp.engine.core.edge.runtime.WireRuntime;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.flow.Flow;
import com.fbp.engine.core.node.model.Node;
import com.fbp.engine.core.node.runtime.NodeRuntime;
import com.fbp.engine.core.port.InputPort;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class FlowRuntime {
    @Getter
    private final Flow flow;
    private final List<NodeRuntime> nodeRuntimes;
    private final List<WireRuntime> wireRuntimes;
    private final ReentrantLock lifecycleLock;
    private FlowRuntimeState state;
    private EngineException lastFailure;

    public FlowRuntime(Flow flow) {
        this.flow = flow;
        this.nodeRuntimes = createNodeRuntimes(flow);
        this.wireRuntimes = createWireRuntimes(flow);
        this.lifecycleLock = new ReentrantLock();
        this.state = FlowRuntimeState.READY;
    }

    public void start(ExecutorService executorService) {
        lifecycleLock.lock();
        try {
            if (state == FlowRuntimeState.RUNNING) {
                return;
            }

            lastFailure = null;
            state = FlowRuntimeState.RUNNING;

            try {
                startRuntimes(executorService);
            } catch (RuntimeException e) {
                state = FlowRuntimeState.FAILED;
                lastFailure = new FlowRuntimeException(flow.getId(), e);
                stopRuntimes();
                throw e;
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stop() {
        lifecycleLock.lock();
        try {
            state = FlowRuntimeState.STOPPED;
            stopRuntimes();
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void fail(EngineException exception) {
        lifecycleLock.lock();
        try {
            if (state != FlowRuntimeState.RUNNING) {
                return;
            }

            lastFailure = exception;
            state = FlowRuntimeState.FAILED;
            stopRuntimes();
        } finally {
            lifecycleLock.unlock();
        }
    }

    public boolean isRunning() {
        lifecycleLock.lock();
        try {
            return state == FlowRuntimeState.RUNNING;
        } finally {
            lifecycleLock.unlock();
        }
    }

    public FlowRuntimeState getState() {
        lifecycleLock.lock();
        try {
            return state;
        } finally {
            lifecycleLock.unlock();
        }
    }

    public EngineException getLastFailure() {
        lifecycleLock.lock();
        try {
            return lastFailure;
        } finally {
            lifecycleLock.unlock();
        }
    }

    private List<NodeRuntime> createNodeRuntimes(Flow flow) {
        List<NodeRuntime> runtimes = new ArrayList<>();
        for (Node node : flow.getNodes().values()) {
            runtimes.add(new NodeRuntime(node, this));
        }
        return List.copyOf(runtimes);
    }

    private List<WireRuntime> createWireRuntimes(Flow flow) {
        List<WireRuntime> runtimes = new ArrayList<>();
        for (Edge edge : flow.getEdges()) {
            InputPort targetInputPort = flow.getNodes()
                    .get(edge.targetNodeId())
                    .getInputPort(edge.targetPortName());
            runtimes.add(new WireRuntime(edge, targetInputPort, this));
        }
        return List.copyOf(runtimes);
    }

    private void startRuntimes(ExecutorService executorService) {
        nodeRuntimes.forEach(runtime -> runtime.start(executorService));
        wireRuntimes.forEach(runtime -> runtime.start(executorService));
    }

    private void stopRuntimes() {
        nodeRuntimes.forEach(NodeRuntime::stop);
        wireRuntimes.forEach(WireRuntime::stop);
    }
}
