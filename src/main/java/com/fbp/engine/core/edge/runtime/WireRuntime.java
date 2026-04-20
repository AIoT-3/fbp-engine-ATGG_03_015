package com.fbp.engine.core.edge.runtime;

import com.fbp.engine.core.edge.Edge;
import com.fbp.engine.core.flow.runtime.FlowRuntime;
import com.fbp.engine.core.port.InputPort;
import com.fbp.engine.core.runtime.RuntimeFailureSupport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class WireRuntime implements Runnable {
    private final Edge edge;
    private final InputPort targetInputPort;
    private final FlowRuntime owner;
    private Future<?> executionHandle;

    public WireRuntime(Edge edge, InputPort targetInputPort, FlowRuntime owner) {
        this.edge = edge;
        this.targetInputPort = targetInputPort;
        this.owner = owner;
    }

    public void start(ExecutorService executorService) {
        executionHandle = executorService.submit(this);
    }

    public void stop() {
        if (executionHandle != null) {
            executionHandle.cancel(true);
            executionHandle = null;
        }
    }

    public void dispatchNext() {
        targetInputPort.receive(edge.connection().take());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                dispatchNext();
            }
        } catch (RuntimeException exception) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            owner.fail(RuntimeFailureSupport.normalize(
                    exception,
                    owner.getFlow().getId())
            );
        }
    }
}
