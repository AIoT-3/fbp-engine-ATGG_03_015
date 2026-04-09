package com.fbp.engine.engine.task;

import java.util.List;

public record FlowTasks(
        List<NodeTask> nodeTasks,
        List<EdgeDispatchTask> edgeDispatchTasks
) {
}
