package com.fbp.engine.engine.task;

import com.fbp.engine.node.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NodeTask implements Runnable {
    private final Node node;

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                node.process(node.takeInput());
            }
        } catch (IllegalStateException e) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            throw e;
        }
    }
}
