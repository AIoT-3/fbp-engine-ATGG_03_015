package com.fbp.engine.runner.stage1;

import com.fbp.engine.engine.FlowEngine;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.impl.FilterNode;
import com.fbp.engine.node.impl.LogNode;
import com.fbp.engine.node.impl.PrintNode;
import com.fbp.engine.node.impl.TimerNode;

public class A1S802 {
    public static void main(String[] args) {
        FlowEngine engine = new FlowEngine();

        Flow flow = new Flow("monitoring")
                .addNode(new TimerNode("timer", 1000))
                .addNode(new LogNode("logger"))
                .addNode(new FilterNode("filter", "tick", 3))
                .addNode(new PrintNode("printer"))
                .connect("timer", "out", "logger", "in")
                .connect("logger", "out", "filter", "in")
                .connect("filter", "out", "printer", "in");

        engine.register(flow);
        engine.startFlow("monitoring");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            engine.shutdown();
        }
    }
}
