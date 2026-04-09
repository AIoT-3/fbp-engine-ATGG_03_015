package com.fbp.engine.engine.task;

import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.Flow;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EdgeDispatchTask implements Runnable {
    private final Flow flow;
    private final Edge edge;

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                flow.getNodes()
                        .get(edge.targetNodeId())
                        .getInputPort(edge.targetPortName())
                        .receive(edge.connection().poll());
            }
        } catch (IllegalStateException e) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            throw e;
        }
    }
}
