package com.fbp.engine.core.runtime;

import com.fbp.engine.common.exception.FbpException;
import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;

import java.util.Objects;

public final class RuntimeFailureSupport {
    private RuntimeFailureSupport() {
        /* This utility class should not be instantiated */
    }

    public static RuntimeException normalize(RuntimeException exception, String flowId) {
        Objects.requireNonNull(exception, "예외는 null일 수 없습니다.");
        if (exception instanceof FbpException) {
            return exception;
        }

        return new EngineException(EngineFailureType.FLOW_RUNTIME_FAILED, exception, flowId);
    }
}
