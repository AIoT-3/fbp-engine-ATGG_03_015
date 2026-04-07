package com.fbp.engine.assignment;

import com.fbp.engine.core.Connection;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;

public class A1S310 {
    public static void main(String[] args) {
        GeneratorNode generatorNode = new GeneratorNode("generator");
        FilterNode filterNode = new FilterNode("filter", "temperature", 30);
        Connection filterConnection = new Connection("filterConnection");
        generatorNode.getOutputPort("out").connect(filterConnection);

        PrintNode printNode1 = new PrintNode("printer1");
        Connection connection1 = new Connection("connection1");
        filterNode.getOutputPort("out").connect(connection1);

        generatorNode.generate("temperature", 25.0);
        filterNode.process(filterConnection.poll());

        generatorNode.generate("temperature", 35.0);
        filterNode.process(filterConnection.poll());
        printNode1.process(connection1.poll());
    }
}
