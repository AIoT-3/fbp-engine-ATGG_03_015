package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.SplitNode;
import com.fbp.engine.node.TimerNode;

public class A1S604 {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        TimerNode timerNode = new TimerNode("timer", 1000);
        SplitNode splitNode = new SplitNode("split", "tick", 3);
        PrintNode matchPrinter = new PrintNode("matchPrinter");
        PrintNode mismatchPrinter = new PrintNode("mismatchPrinter");

        Connection connection1 = new Connection("timer-to-split");
        Connection connection2 = new Connection("split-to-matchPrinter");
        Connection connection3 = new Connection("split-to-mismatchPrinter");

        timerNode.getOutputPort("out").connect(connection1);
        splitNode.getOutputPort("match").connect(connection2);
        splitNode.getOutputPort("mismatch").connect(connection3);

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

        timerNode.initialize();

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;

            timerNode.shutdown();

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
