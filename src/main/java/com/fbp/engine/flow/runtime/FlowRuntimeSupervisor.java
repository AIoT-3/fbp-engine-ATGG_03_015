package com.fbp.engine.flow.runtime;

import com.fbp.engine.engine.exception.FlowTaskExecutionException;
import com.fbp.engine.exception.EngineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class FlowRuntimeSupervisor implements Runnable {
    private final FlowRuntime runtime;
    private final List<Future<?>> taskFutures;

    @Override
    public void run() {
        for (Future<?> task : taskFutures) {
            try {
                task.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (CancellationException e) {
                if (runtime.getState() == FlowRuntimeState.STOPPED) {
                    return;
                }
            } catch (ExecutionException e) {
                handleTaskFailure(e.getCause());
                return;
            }
        }
    }

    private void handleTaskFailure(Throwable cause) {
        EngineException failure = cause instanceof EngineException engineException
                ? engineException
                : new FlowTaskExecutionException(runtime.getFlow().getId(), cause);
        log.error("[Runtime] 플로우 '{}' task 실패", runtime.getFlow().getId(), cause);
        runtime.fail(failure);
    }
}
