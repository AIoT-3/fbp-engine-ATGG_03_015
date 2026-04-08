package com.fbp.engine.runner;

import com.fbp.engine.connection.Connection;
import com.fbp.engine.node.impl.GeneratorNode;
import com.fbp.engine.node.impl.PrintNode;

public class A1S404 {
    public static void main(String[] args) {

        GeneratorNode generatorNode = new GeneratorNode("generator");
        PrintNode printNode = new PrintNode("printer");
        Connection connection = new Connection("generator-to-printer");
        generatorNode.getOutputPort("out").connect(connection);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                generatorNode.generate("temperature", 20.0 + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                printNode.process(connection.poll());
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
