package com.fbp.engine.runner.stage1;

import com.fbp.engine.engine.FlowEngine;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.impl.PrintNode;
import com.fbp.engine.node.impl.TimerNode;

public class A1S803 {
    public static void main(String[] args) {
        FlowEngine engine = new FlowEngine();

        Flow flowA = new Flow("flowA")
                .addNode(new TimerNode("timerA", 500))
                .addNode(new PrintNode("printerA"))
                .connect("timerA", "out", "printerA", "in");

        Flow flowB = new Flow("flowB")
                .addNode(new TimerNode("timerB", 1000))
                .addNode(new PrintNode("printerB"))
                .connect("timerB", "out", "printerB", "in");

        engine.register(flowA);
        engine.register(flowB);
        engine.startFlow("flowA");
        engine.startFlow("flowB");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            engine.shutdown();
        }
    }
}
