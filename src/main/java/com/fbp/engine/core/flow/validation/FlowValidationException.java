package com.fbp.engine.core.flow.validation;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public class FlowValidationException extends EngineException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<FlowValidationFailure> errors;

    public FlowValidationException(String flowId, List<FlowValidationFailure> errors) {
        super(EngineFailureType.FLOW_VALIDATION_FAILED, flowId, errors.size());
        this.errors = List.copyOf(errors);
    }
}
