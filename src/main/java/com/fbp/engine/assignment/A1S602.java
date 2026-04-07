package com.fbp.engine.assignment;

import com.fbp.engine.core.Connection;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;
import com.fbp.engine.node.TransformNode;

import java.util.Map;

public class A1S602 {
    public static void main(String[] args) {
        GeneratorNode generatorNode = new GeneratorNode("generator");
        TransformNode transformNode = new TransformNode("transform", message -> {
            // 화씨 -> 섭씨 변환
            if (!message.hasKey("temperature")) {
                return null;
            }
            double tempF = message.get("temperature");
            double tempC = (tempF - 32) * 5.0 / 9.0;
            return Message.of(Map.of("temperature", tempC));
        });
        PrintNode printNode = new PrintNode("printer");

        Connection connection1 = new Connection("generator-to-transform");
        Connection connection2 = new Connection("transform-to-printer");

        generatorNode.getOutputPort("out").connect(connection1);
        transformNode.getOutputPort("out").connect(connection2);

        generatorNode.generate("temperature", 77.0);
        transformNode.process(connection1.poll());
        printNode.process(connection2.poll());
    }
}
