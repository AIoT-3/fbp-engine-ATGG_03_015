package com.fbp.engine.core.edge.runtime;

import com.fbp.engine.core.flow.exception.FlowRuntimeException;
import com.fbp.engine.core.edge.Edge;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.flow.runtime.FlowRuntime;
import com.fbp.engine.core.port.InputPort;

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

    public void dispatch() {
        targetInputPort.receive(edge.connection().poll());
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                dispatch();
            }
        } catch (RuntimeException exception) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            owner.fail(toEngineException(exception));
        }
    }

    private EngineException toEngineException(Throwable throwable) {
        if (throwable instanceof EngineException engineException) {
            return engineException;
        }
        return new FlowRuntimeException(owner.getFlow().getId(), throwable);
    }
}
