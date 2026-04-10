package com.fbp.engine.engine;

import com.fbp.engine.flow.runtime.FlowRuntime;
import com.fbp.engine.flow.runtime.FlowRuntimeState;
import com.fbp.engine.engine.task.FlowTasks;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.flow.exception.FlowNotFoundException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Getter
public class FlowEngine {
    private final Map<String, FlowRuntime> runtimes;
    private final ExecutorService executorService;

    public FlowEngine() {
        this.runtimes = new HashMap<>();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void register(Flow flow) {
        runtimes.put(flow.getId(), new FlowRuntime(flow));
        log.info("[Engine] 플로우 '{}' 등록됨", flow.getId());
    }

    public void startFlow(String flowId) {
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new FlowNotFoundException(flowId);
        }
        Flow flow = runtime.getFlow();

        List<String> errors = flow.validate();
        if (!errors.isEmpty()) {
            log.error("[Engine] 플로우 '{}' 검증 실패: {}", flowId, errors);
            return;
        }

        if (runtime.isRunning()) {
            log.warn("[Engine] 플로우 '{}' 이미 실행 중", flowId);
            return;
        }

        FlowTasks flowTasks = FlowTaskFactory.createTasks(flow);
        runtime.start(flowTasks, executorService);
        log.info("[Engine] 플로우 '{}' 시작됨", flowId);
    }

    public void stopFlow(String flowId) {
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new FlowNotFoundException(flowId);
        }

        runtime.stop();
        log.info("[Engine] 플로우 '{}' 정지됨", flowId);
    }

    public void shutdown() {
        runtimes.values().forEach(FlowRuntime::stop);
        runtimes.clear();
        executorService.shutdownNow();
        log.info("[Engine] 모든 플로우가 종료됨");
    }

    public void listFlows() {
        log.info("[Engine] 등록된 플로우 목록:");
        for (FlowRuntime runtime : runtimes.values()) {
            log.info("[{}]: {}", runtime.getFlow().getId(), runtime.getState());
        }
    }

    public FlowEngineState getState() {
        if (executorService.isShutdown()) {
            return FlowEngineState.STOPPED;
        }

        if (runtimes.values().stream().anyMatch(FlowRuntime::isRunning)) {
            return FlowEngineState.RUNNING;
        }

        return runtimes.values().stream().allMatch(runtime -> runtime.getState() == FlowRuntimeState.READY)
                ? FlowEngineState.INITIALIZED
                : FlowEngineState.STOPPED;
    }
}
