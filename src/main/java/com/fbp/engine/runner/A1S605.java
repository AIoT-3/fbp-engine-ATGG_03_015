package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.LogNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TimerNode;

public class A1S605 {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        TimerNode timerNode = new TimerNode("timer", 1000);
        LogNode logNode = new LogNode("logger");
        FilterNode filterNode = new FilterNode("filter", "tick", 3);
        PrintNode printNode = new PrintNode("printer");

        Connection connection1 = new Connection("timer-to-logger");
        Connection connection2 = new Connection("logger-to-filter");
        Connection connection3 = new Connection("filter-to-printer");

        timerNode.getOutputPort("out").connect(connection1);
        logNode.getOutputPort("out").connect(connection2);
        filterNode.getOutputPort("out").connect(connection3);

        Thread logThread = new Thread(() -> {
            while (running || connection1.getBufferSize() > 0) {
                try {
                    logNode.process(connection1.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "log-thread");

        Thread filterThread = new Thread(() -> {
            while (running || connection2.getBufferSize() > 0) {
                try {
                    filterNode.process(connection2.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "filter-thread");

        Thread printThread = new Thread(() -> {
            while (running || connection3.getBufferSize() > 0) {
                try {
                    printNode.process(connection3.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "print-thread");

        logThread.start();
        filterThread.start();
        printThread.start();

        timerNode.initialize();
        logNode.initialize();
        filterNode.initialize();
        printNode.initialize();

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;

            timerNode.shutdown();
            logNode.shutdown();
            filterNode.shutdown();
            printNode.shutdown();

            logThread.interrupt();
            filterThread.interrupt();
            printThread.interrupt();

            try {
                logThread.join();
                filterThread.join();
                printThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
