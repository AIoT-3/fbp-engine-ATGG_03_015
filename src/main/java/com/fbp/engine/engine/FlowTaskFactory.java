package com.fbp.engine.engine;

import com.fbp.engine.edge.Edge;
import com.fbp.engine.engine.task.EdgeDispatchTask;
import com.fbp.engine.engine.task.FlowTasks;
import com.fbp.engine.engine.task.NodeTask;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.Node;

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
            edgeDispatchTasks.add(new EdgeDispatchTask(flow, edge));
        }

        return new FlowTasks(nodeTasks, edgeDispatchTasks);
    }
}
