package com.fbp.engine.node.builtin.source;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import com.fbp.engine.node.NodeExecutionMode;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerNode extends AbstractNode {
    private final long intervalMs;
    private int tickCount = 0;
    private ScheduledExecutorService scheduler;

    public TimerNode(String id, long intervalMs) {
        super(id);
        this.intervalMs = intervalMs;
        addOutputPort("out");
    }

    @Override
    public NodeExecutionMode executionMode() {
        return NodeExecutionMode.SELF_DRIVEN;
    }

    @Override
    public void initialize() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Message msg = Message.of(Map.of("tick", tickCount, "timestamp", System.currentTimeMillis()));
            send("out", msg);
            tickCount++;
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        this.scheduler.shutdown();
    }

    @Override
    public void onProcess(PortMessage portMessage) {

    }
}
