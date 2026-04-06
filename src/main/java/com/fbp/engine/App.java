package com.fbp.engine;

import com.fbp.engine.core.Connection;
import com.fbp.engine.core.DefaultInputPort;
import com.fbp.engine.core.DefaultOutputPort;
import com.fbp.engine.node.FilterNode;
import com.fbp.engine.node.GeneratorNode;
import com.fbp.engine.node.PrintNode;

public class App {
    public static void main(String[] args) {
        GeneratorNode generatorNode = new GeneratorNode("generator");
        generatorNode.setOutputPort(new DefaultOutputPort("out"));

        FilterNode filterNode = new FilterNode("filter", "temperature", 30);
        filterNode.setInputPort(new DefaultInputPort("in", filterNode));
        Connection filterConnection = new Connection("filterConnection");
        filterConnection.setTarget(filterNode.getInputPort());
        generatorNode.getOutputPort().connect(filterConnection);
        filterNode.setOutputPort(new DefaultOutputPort("out"));

        PrintNode printNode1 = new PrintNode("printer1");
        Connection connection1 = new Connection("connection1");
        connection1.setTarget(printNode1.getInputPort());
        filterNode.getOutputPort().connect(connection1);

        generatorNode.generate("temperature", 235.5);
    }
}
