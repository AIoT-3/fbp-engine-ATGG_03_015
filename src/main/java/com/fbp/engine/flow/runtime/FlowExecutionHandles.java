package com.fbp.engine.flow.runtime;

import java.util.List;
import java.util.concurrent.Future;

public record FlowExecutionHandles(
        List<Future<?>> taskFutures,
        Future<?> supervisorFuture
) {
    public static FlowExecutionHandles empty() {
        return new FlowExecutionHandles(List.of(), null);
    }

    public void cancelAll() {
        taskFutures.forEach(task -> task.cancel(true));
        if (supervisorFuture != null) {
            supervisorFuture.cancel(true);
        }
    }

    public boolean isEmpty() {
        return taskFutures.isEmpty() && supervisorFuture == null;
    }
}
