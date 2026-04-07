package com.fbp.engine.runner;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TimerNode;

public class A1S506 {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        TimerNode timerNode = new TimerNode("timer", 500);
        FilterNode filterNode = new FilterNode("filter", "tick", 3);
        PrintNode printNode = new PrintNode("printer");

        Connection connection1 = new Connection("timer-to-filter");
        Connection connection2 = new Connection("filter-to-printer");

        timerNode.getOutputPort("out").connect(connection1);
        filterNode.getOutputPort("out").connect(connection2);

        Thread filterThread = new Thread(() -> {
            while (running || connection1.getBufferSize() > 0) {
                try {
                    filterNode.process(connection1.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "filter-thread");

        Thread printerThread = new Thread(() -> {
            while (running || connection2.getBufferSize() > 0) {
                try {
                    printNode.process(connection2.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        }, "printer-thread");

        filterThread.start();
        printerThread.start();

        timerNode.initialize();
        filterNode.initialize();
        printNode.initialize();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            running = false;

            timerNode.shutdown();
            filterNode.shutdown();
            printNode.shutdown();

            filterThread.interrupt();
            printerThread.interrupt();

            try {
                filterThread.join();
                printerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
