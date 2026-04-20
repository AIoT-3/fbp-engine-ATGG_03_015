package com.fbp.engine.core.flow.runtime;

import com.fbp.engine.core.flow.Flow;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.flow.validation.FlowValidationFailure;
import com.fbp.engine.core.flow.validation.FlowValidationException;
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
        // flow 중복 등록 방지
        if (runtimes.containsKey(flow.getId())) {
            throw new EngineException(EngineFailureType.FLOW_ALREADY_REGISTERED, flow.getId());
        }

        runtimes.put(flow.getId(), new FlowRuntime(flow));
        log.info("[Engine] 플로우 '{}' 등록됨", flow.getId());
    }

    public void startFlow(String flowId) {
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new EngineException(EngineFailureType.FLOW_NOT_FOUND_BY_ID, flowId);
        }
        Flow flow = runtime.getFlow();

        List<FlowValidationFailure> errors = flow.validate();
        if (!errors.isEmpty()) {
            log.error("[Engine] 플로우 '{}' 검증 실패: {}", flowId, errors);
            throw new FlowValidationException(flowId, errors);
        }

        if (runtime.isRunning()) {
            log.warn("[Engine] 플로우 '{}' 이미 실행 중", flowId);
            return;
        }

        runtime.start(executorService);
        log.info("[Engine] 플로우 '{}' 시작됨", flowId);
    }

    public void stopFlow(String flowId) {
        FlowRuntime runtime = runtimes.get(flowId);
        if (runtime == null) {
            throw new EngineException(EngineFailureType.FLOW_NOT_FOUND_BY_ID, flowId);
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
