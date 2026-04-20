package com.fbp.engine.core.flow.validation;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;

public record FlowValidationError(
        EngineFailureType failureType,
        String message
) {
    public static FlowValidationError from(EngineException exception) {
        return new FlowValidationError(exception.getFailureType(), exception.getMessage());
    }
}
