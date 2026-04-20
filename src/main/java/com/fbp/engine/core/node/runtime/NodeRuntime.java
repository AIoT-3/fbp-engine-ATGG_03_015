package com.fbp.engine.core.node.runtime;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineExceptionSupport;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.flow.runtime.FlowRuntime;
import com.fbp.engine.core.node.model.InboxNode;
import com.fbp.engine.core.node.model.Node;
import com.fbp.engine.core.node.model.NodeExecutionMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class NodeRuntime implements Runnable {
    private final Node node;
    private final FlowRuntime owner;
    private final ReentrantLock lifecycleLock;
    private Future<?> executionHandle;
    private boolean initialized;
    private NodeRuntimeState state;
    private EngineException lastFailure;

    public NodeRuntime(Node node, FlowRuntime owner) {
        this.node = node;
        this.owner = owner;
        this.lifecycleLock = new ReentrantLock();
        this.state = NodeRuntimeState.READY;
    }

    public void start(ExecutorService executorService) {
        lifecycleLock.lock();
        try {
            lastFailure = null;

            node.initialize();
            initialized = true;

            if (node.executionMode() != NodeExecutionMode.INBOX_DRIVEN) {
                executionHandle = null;
                state = NodeRuntimeState.RUNNING;
                return;
            }

            if (!(node instanceof InboxNode)) {
                throw new EngineException(EngineFailureType.INVALID_NODE_EXECUTION_MODE, node.getId());
            }

            executionHandle = executorService.submit(this);
            state = NodeRuntimeState.RUNNING;
        } catch (RuntimeException exception) {
            EngineException failure = EngineExceptionSupport.toEngineException(
                    exception, EngineFailureType.FLOW_RUNTIME_FAILED, owner.getFlow().getId());
            lastFailure = failure;
            state = NodeRuntimeState.FAILED;
            throw failure;
        } finally {
            lifecycleLock.unlock();
        }
    }

    public void stop() {
        lifecycleLock.lock();
        try {
            if (executionHandle != null) {
                executionHandle.cancel(true);
                executionHandle = null;
            }

            if (initialized) {
                node.shutdown();
                initialized = false;
            }

            if (state != NodeRuntimeState.FAILED) {
                state = NodeRuntimeState.STOPPED;
            }
        } finally {
            lifecycleLock.unlock();
        }
    }

    public NodeRuntimeState getState() {
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

    @Override
    public void run() {
        InboxNode inboxNode = (InboxNode) node;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                node.process(inboxNode.takeInput());
            }
        } catch (RuntimeException exception) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            recordFailure(EngineExceptionSupport.toEngineException(
                    exception, EngineFailureType.FLOW_RUNTIME_FAILED, owner.getFlow().getId()));
        }
    }

    private void recordFailure(EngineException failure) {
        lifecycleLock.lock();
        try {
            lastFailure = failure;
            state = NodeRuntimeState.FAILED;
        } finally {
            lifecycleLock.unlock();
        }
        owner.fail(failure);
    }
}
