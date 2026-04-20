package com.fbp.engine.core.flow.validation;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;

import java.io.Serial;
import java.io.Serializable;

public record FlowValidationFailure(
        EngineFailureType failureType,
        String message
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static FlowValidationFailure from(EngineException exception) {
        return new FlowValidationFailure(exception.getFailureType(), exception.getMessage());
    }
}
