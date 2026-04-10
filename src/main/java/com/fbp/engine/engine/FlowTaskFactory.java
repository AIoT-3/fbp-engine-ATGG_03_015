package com.fbp.engine.engine;

import com.fbp.engine.edge.Edge;
import com.fbp.engine.edge.connection.WireRuntime;
import com.fbp.engine.engine.task.EdgeDispatchTask;
import com.fbp.engine.engine.task.FlowTasks;
import com.fbp.engine.engine.task.NodeTask;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.Node;
import com.fbp.engine.port.InputPort;

import java.util.ArrayList;
import java.util.List;

public class FlowTaskFactory {
    private FlowTaskFactory() {
        /* This utility class should not be instantiated */
    }

    public static FlowTasks createTasks(Flow flow) {
        List<NodeTask> nodeTasks = new ArrayList<>();
        for (Node node : flow.getNodes().values()) {
            nodeTasks.add(new NodeTask(node));
        }

        List<EdgeDispatchTask> edgeDispatchTasks = new ArrayList<>();
        for (Edge edge : flow.getEdges()) {
            InputPort targetInputPort = flow.getNodes()
                    .get(edge.targetNodeId())
                    .getInputPort(edge.targetPortName());
            edgeDispatchTasks.add(new EdgeDispatchTask(new WireRuntime(edge, targetInputPort)));
        }

        return new FlowTasks(nodeTasks, edgeDispatchTasks);
    }
}
