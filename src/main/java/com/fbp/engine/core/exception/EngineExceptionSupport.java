package com.fbp.engine.core.exception;

public final class EngineExceptionSupport {
    private EngineExceptionSupport() {
        /* This utility class should not be instantiated */
    }

    public static EngineException toEngineException(
            Throwable throwable,
            EngineFailureType fallbackType,
            Object... args
    ) {
        if (throwable instanceof EngineException engineException) {
            return engineException;
        }
        return new EngineException(fallbackType, throwable, args);
    }
}
