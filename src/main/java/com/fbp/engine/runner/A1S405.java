package com.fbp.engine.runner;

import com.fbp.engine.connection.Connection;
import com.fbp.engine.node.impl.FilterNode;
import com.fbp.engine.node.impl.GeneratorNode;
import com.fbp.engine.node.impl.PrintNode;

public class A1S405 {
    private static volatile boolean running = true;

    public static void main(String[] args) {
        GeneratorNode generatorNode = new GeneratorNode("generator");
        Connection connection1 = new Connection("connection1");
        FilterNode filterNode = new FilterNode("filter", "temperature", 30);
        Connection connection2 = new Connection("connection2");
        PrintNode printNode = new PrintNode("printer");

        generatorNode.getOutputPort("out").connect(connection1);
        filterNode.getOutputPort("out").connect(connection2);

        Thread generatorThread = new Thread(() -> {
            double[] temperatures = {25.0, 35.0, 28.0, 40.0, 32.0};

            for (double temperature : temperatures) {
                generatorNode.generate("temperature", temperature);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        Thread filterThread = new Thread(() -> {
            while (running || connection1.getBufferSize() > 0) {
                try {
                    filterNode.process(connection1.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        });

        Thread printerThread = new Thread(() -> {
            while (running || connection2.getBufferSize() > 0) {
                try {
                    printNode.process(connection2.poll());
                } catch (IllegalStateException e) {
                    return;
                }
            }
        });

        generatorThread.start();
        filterThread.start();
        printerThread.start();

        try {
            generatorThread.join();

            while (connection1.getBufferSize() > 0 || connection2.getBufferSize() > 0) {
                Thread.sleep(100);
            }

            running = false;
            filterThread.interrupt();
            printerThread.interrupt();

            filterThread.join();
            printerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
