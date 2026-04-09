package com.fbp.engine.engine;

import com.fbp.engine.flow.Flow;
import com.fbp.engine.engine.task.FlowTasks;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Getter
public class FlowRuntime {
    private final Flow flow;
    private final List<Future<?>> tasks;

    public FlowRuntime(Flow flow) {
        this.flow = flow;
        this.tasks = new ArrayList<>();
    }

    public void start(FlowTasks flowTasks, ExecutorService executorService) {
        tasks.clear();

        flowTasks.nodeTasks().forEach(nodeTask -> tasks.add(executorService.submit(nodeTask)));
        flowTasks.edgeDispatchTasks().forEach(edgeTask -> tasks.add(executorService.submit(edgeTask)));
    }

    public void stop() {
        tasks.forEach(task -> task.cancel(true));
        tasks.clear();
    }
}
