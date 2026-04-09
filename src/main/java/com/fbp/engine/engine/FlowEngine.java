package com.fbp.engine.engine;

import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.flow.FlowState;
import com.fbp.engine.flow.exception.FlowNotFoundException;
import com.fbp.engine.node.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Getter
public class FlowEngine {
    private final Map<String, Flow> flows;
    private final Map<String, List<Future<?>>> flowTasks;
    private final ExecutorService executorService;
    private EngineState state;

    public FlowEngine() {
        this.flows = new HashMap<>();
        this.flowTasks = new HashMap<>();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.state = EngineState.INITIALIZED;
    }

    public void register(Flow flow) {
        flows.put(flow.getId(), flow);
        log.info("[Engine] 플로우 '{}' 등록됨", flow.getId());
    }

    public void startFlow(String flowId) {
        // 1. 플로우 조회
        Flow flow = flows.get(flowId);
        if (flow == null) {
            throw new FlowNotFoundException(flowId);
        }

        // 2. 플로우 검증
        List<String> errors = flow.validate();
        if (!errors.isEmpty()) {
            log.error("[Engine] 플로우 '{}' 검증 실패: {}", flowId, errors);
            return;
        }

        // 3. 플로우 상태 확인
        if (flow.getState() == FlowState.RUNNING) {
            log.warn("[Engine] 플로우 '{}' 이미 실행 중", flowId);
            return;
        }

        // 4. 플로우 실행
        flow.initialize();
        List<Future<?>> tasks = new ArrayList<>();
        for (Edge edge : flow.getEdges()) {
            Node targetNode = flow.getNodes().get(edge.targetNodeId());
            Future<?> task = executorService.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        targetNode.process(edge.connection().poll());
                    }
                } catch (IllegalStateException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        return; // interrupted 상태는 정상적인 종료로 간주
                    }
                    throw e;
                }
            });
            tasks.add(task);
        }
        flowTasks.put(flowId, tasks);

        // 5. 플로우 및 엔진 상태 업데이트
        flow.setState(FlowState.RUNNING);
        if (this.state != EngineState.RUNNING) {
            this.state = EngineState.RUNNING;
        }
        log.info("[Engine] 플로우 '{}' 시작됨", flowId);
    }

    public void stopFlow(String flowId) {
        // 1. 플로우 조회
        Flow flow = flows.get(flowId);
        if (flow == null) {
            throw new FlowNotFoundException(flowId);
        }

        // 2. task 취소
        List<Future<?>> tasks = flowTasks.remove(flowId);
        if (tasks != null) {
            tasks.forEach(task -> task.cancel(true));
        }

        // 3. 플로우 종료
        flow.shutdown();
        flow.setState(FlowState.STOPPED);
        log.info("[Engine] 플로우 '{}' 정지됨", flowId);

        // 4. 엔진 상태 업데이트
        if (flows.values().stream().noneMatch(f -> f.getState() == FlowState.RUNNING)) {
            this.state = EngineState.STOPPED;
        }
    }

    public void shutdown() {
        // 1. 모든 task 취소
        flowTasks.values().stream()
                .flatMap(List::stream)
                .forEach(task -> task.cancel(true));

        // 2. 모든 플로우 종료
        for (Flow flow : flows.values()) {
            flow.shutdown();
        }

        // 3. 모든 플로우 상태 업데이트
        flows.values().stream()
                .filter(flow -> flow.getState() == FlowState.RUNNING)
                .forEach(flow -> flow.setState(FlowState.STOPPED));

        // 4. flowTasks 및 executorService 정리
        flowTasks.clear();
        executorService.shutdownNow();

        // 5. 엔진 상태 업데이트
        this.state = EngineState.STOPPED;
        log.info("[Engine] 모든 플로우가 종료됨");
    }

    public void listFlows() {
        log.info("[Engine] 등록된 플로우 목록:");
        for (Flow flow : flows.values()) {
            log.info("[{}]: {}", flow.getId(), flow.getState());
        }
    }
}
