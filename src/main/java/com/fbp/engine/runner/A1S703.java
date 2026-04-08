package com.fbp.engine.runner;

import com.fbp.engine.connection.Connection;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.impl.PrintNode;
import com.fbp.engine.node.impl.SplitNode;
import com.fbp.engine.node.impl.TimerNode;

public class A1S703 {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        TimerNode timerNode = new TimerNode("timer", 1000);
        SplitNode splitNode = new SplitNode("split", "tick", 3);
        PrintNode matchPrinter = new PrintNode("matchPrinter");
        PrintNode mismatchPrinter = new PrintNode("mismatchPrinter");

        Flow flow = new Flow("timer-split-branch")
                .addNode(timerNode)
                .addNode(splitNode)
                .addNode(matchPrinter)
                .addNode(mismatchPrinter)
                .connect("timer", "out", "split", "in")
                .connect("split", "match", "matchPrinter", "in")
                .connect("split", "mismatch", "mismatchPrinter", "in");

        Connection connection1 = flow.getConnections().get(0);
        Connection connection2 = flow.getConnections().get(1);
        Connection connection3 = flow.getConnections().get(2);

        Thread splitThread = new Thread(() -> {
            while (running || connection1.getBufferSize() > 0) {
                try {
                    splitNode.process(connection1.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "split-thread");

        Thread matchThread = new Thread(() -> {
            while (running || connection2.getBufferSize() > 0) {
                try {
                    matchPrinter.process(connection2.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "match-thread");

        Thread mismatchThread = new Thread(() -> {
            while (running || connection3.getBufferSize() > 0) {
                try {
                    mismatchPrinter.process(connection3.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "mismatch-thread");

        splitThread.start();
        matchThread.start();
        mismatchThread.start();

        flow.initialize();

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;
            flow.shutdown();

            splitThread.interrupt();
            matchThread.interrupt();
            mismatchThread.interrupt();

            try {
                splitThread.join();
                matchThread.join();
                mismatchThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
