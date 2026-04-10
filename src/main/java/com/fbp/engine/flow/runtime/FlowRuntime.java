package com.fbp.engine.flow.runtime;

import com.fbp.engine.engine.exception.FlowTaskExecutionException;
import com.fbp.engine.engine.task.FlowTasks;
import com.fbp.engine.exception.EngineException;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.Node;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class FlowRuntime {
    @Getter
    private final Flow flow;
    private final ReentrantLock lifecycleLock;
    private FlowRuntimeState state;
    private EngineException lastFailure;
    private FlowExecutionHandles executionHandles;

    public FlowRuntime(Flow flow) {
        this.flow = flow;
        this.lifecycleLock = new ReentrantLock();
        this.state = FlowRuntimeState.READY;
        this.executionHandles = FlowExecutionHandles.empty();
    }

    public void start(FlowTasks flowTasks, ExecutorService executorService) {
        lifecycleLock.lock();
        try {
            if (state == FlowRuntimeState.RUNNING) {
                return;
            }

            executionHandles.cancelAll();
            executionHandles = FlowExecutionHandles.empty();
            lastFailure = null;

            try {
                initializeNodes();
                List<Future<?>> taskFutures = submitTasks(flowTasks, executorService);
                Future<?> supervisorFuture = executorService.submit(new FlowRuntimeSupervisor(this, taskFutures));
                executionHandles = new FlowExecutionHandles(taskFutures, supervisorFuture);
                state = FlowRuntimeState.RUNNING;
            } catch (RuntimeException e) {
                state = FlowRuntimeState.FAILED;
                lastFailure = new FlowTaskExecutionException(flow.getId(), e);
                shutdownNodes();
                throw e;
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stop() {
        lifecycleLock.lock();
        try {
            FlowRuntimeState previousState = state;
            FlowExecutionHandles currentHandles = executionHandles;

            executionHandles = FlowExecutionHandles.empty();
            state = FlowRuntimeState.STOPPED;
            currentHandles.cancelAll();

            if (previousState == FlowRuntimeState.RUNNING) {
                shutdownNodes();
            }
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

            FlowExecutionHandles currentHandles = executionHandles;
            executionHandles = FlowExecutionHandles.empty();
            lastFailure = exception;
            state = FlowRuntimeState.FAILED;
            currentHandles.cancelAll();
            shutdownNodes();
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

    public boolean hasActiveExecution() {
        lifecycleLock.lock();
        try {
            return !executionHandles.isEmpty();
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

    private void initializeNodes() {
        for (Node node : flow.getNodes().values()) {
            node.initialize();
        }
    }

    private void shutdownNodes() {
        for (Node node : flow.getNodes().values()) {
            node.shutdown();
        }
    }

    private List<Future<?>> submitTasks(FlowTasks flowTasks, ExecutorService executorService) {
        List<Future<?>> taskFutures = new java.util.ArrayList<>();
        flowTasks.nodeTasks().forEach(task -> taskFutures.add(executorService.submit(task)));
        flowTasks.edgeDispatchTasks().forEach(task -> taskFutures.add(executorService.submit(task)));
        return List.copyOf(taskFutures);
    }
}
