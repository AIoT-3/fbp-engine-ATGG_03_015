package com.fbp.engine.runner.stage1;

import com.fbp.engine.edge.Connection;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.impl.FilterNode;
import com.fbp.engine.node.impl.LogNode;
import com.fbp.engine.node.impl.PrintNode;
import com.fbp.engine.node.impl.TimerNode;

public class A1S702 {
//    private static volatile boolean running = true;
//
//    public static void main(String[] args) {
//        TimerNode timerNode = new TimerNode("timer", 1000);
//        LogNode logNode = new LogNode("logger");
//        FilterNode filterNode = new FilterNode("filter", "tick", 3);
//        PrintNode printNode = new PrintNode("printer");
//
//        Flow flow = new Flow("timer-log-filter-print")
//                .addNode(timerNode)
//                .addNode(logNode)
//                .addNode(filterNode)
//                .addNode(printNode)
//                .connect("timer", "out", "logger", "in")
//                .connect("logger", "out", "filter", "in")
//                .connect("filter", "out", "printer", "in");
//
//        Connection connection1 = flow.getEdges().get(0).connection();
//        Connection connection2 = flow.getEdges().get(1).connection();
//        Connection connection3 = flow.getEdges().get(2).connection();
//
//        Thread logThread = new Thread(() -> {
//            while (running || connection1.getBufferSize() > 0) {
//                try {
//                    logNode.process(connection1.poll());
//                } catch (IllegalStateException e) {
//                    return;
//                }
//            }
//        }, "log-thread");
//
//        Thread filterThread = new Thread(() -> {
//            while (running || connection2.getBufferSize() > 0) {
//                try {
//                    filterNode.process(connection2.poll());
//                } catch (IllegalStateException e) {
//                    return;
//                }
//            }
//        }, "filter-thread");
//
//        Thread printThread = new Thread(() -> {
//            while (running || connection3.getBufferSize() > 0) {
//                try {
//                    printNode.process(connection3.poll());
//                } catch (IllegalStateException e) {
//                    return;
//                }
//            }
//        }, "print-thread");
//
//        logThread.start();
//        filterThread.start();
//        printThread.start();
//
//        flow.initialize();
//
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } finally {
//            running = false;
//            flow.shutdown();
//
//            logThread.interrupt();
//            filterThread.interrupt();
//            printThread.interrupt();
//
//            try {
//                logThread.join();
//                filterThread.join();
//                printThread.join();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
}
