package com.fbp.engine.engine.task;

import com.fbp.engine.edge.connection.WireRuntime;
import com.fbp.engine.exception.EngineException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EdgeDispatchTask implements Runnable {
    private final WireRuntime wireRuntime;

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                wireRuntime.dispatch();
            }
        } catch (EngineException exception) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            throw exception;
        }
    }
}
