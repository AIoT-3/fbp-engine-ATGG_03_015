package com.fbp.engine.engine;

import com.fbp.engine.engine.task.FlowTasks;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.flow.FlowState;
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
    private EngineState state;

    public FlowEngine() {
        this.runtimes = new HashMap<>();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.state = EngineState.INITIALIZED;
    }

    public void register(Flow flow) {
        runtimes.put(flow.getId(), new FlowRuntime(flow));
        log.info("[Engine] 플로우 '{}' 등록됨", flow.getId());
    }

    public void startFlow(String flowId) {
        // 1. 플로우 조회
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new FlowNotFoundException(flowId);
        }
        Flow flow = runtime.getFlow();

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
        FlowTasks flowTasks = FlowTaskFactory.createTasks(flow);
        runtime.start(flowTasks, executorService);

        // 5. 플로우 및 엔진 상태 업데이트
        flow.setState(FlowState.RUNNING);
        if (this.state != EngineState.RUNNING) {
            this.state = EngineState.RUNNING;
        }
        log.info("[Engine] 플로우 '{}' 시작됨", flowId);
    }

    public void stopFlow(String flowId) {
        // 1. 플로우 조회
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new FlowNotFoundException(flowId);
        }
        Flow flow = runtime.getFlow();

        // 2. task 취소
        runtime.stop();

        // 3. 플로우 종료
        flow.shutdown();
        flow.setState(FlowState.STOPPED);
        log.info("[Engine] 플로우 '{}' 정지됨", flowId);

        // 4. 엔진 상태 업데이트
        if (runtimes.values().stream().noneMatch(r -> r.getFlow().getState() == FlowState.RUNNING)) {
            this.state = EngineState.STOPPED;
        }
    }

    public void shutdown() {
        // 1. 모든 task 취소
        runtimes.values().forEach(FlowRuntime::stop);

        // 2. 모든 플로우 종료
        for (FlowRuntime runtime : runtimes.values()) {
            runtime.getFlow().shutdown();
        }

        // 3. 모든 플로우 상태 업데이트
        runtimes.values().stream()
                .map(FlowRuntime::getFlow)
                .filter(flow -> flow.getState() == FlowState.RUNNING)
                .forEach(flow -> flow.setState(FlowState.STOPPED));

        // 4. flowTasks 및 executorService 정리
        runtimes.clear();
        executorService.shutdownNow();

        // 5. 엔진 상태 업데이트
        this.state = EngineState.STOPPED;
        log.info("[Engine] 모든 플로우가 종료됨");
    }

    public void listFlows() {
        log.info("[Engine] 등록된 플로우 목록:");
        for (FlowRuntime runtime : runtimes.values()) {
            Flow flow = runtime.getFlow();
            log.info("[{}]: {}", flow.getId(), flow.getState());
        }
    }
}
