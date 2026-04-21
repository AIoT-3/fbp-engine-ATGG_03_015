package com.fbp.engine.core.rule;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.Message;

import java.util.List;
import java.util.Objects;

public record CompositeCondition (
        LogicalOperator operator,
        List<MessageCondition> conditions
) implements MessageCondition {

    public CompositeCondition {
        if (operator == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "operator");
        }
        if (conditions == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "conditions");
        }
        if (conditions.stream().anyMatch(Objects::isNull)) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "conditions[*]");
        }
        conditions = List.copyOf(conditions);
    }

    @Override
    public boolean matches(Message message) {
        return switch (operator) {
            case AND -> conditions.stream().allMatch(condition -> condition.matches(message));
            case OR -> conditions.stream().anyMatch(condition -> condition.matches(message));
        };
    }

    public static CompositeCondition and(MessageCondition... conditions) {
        if (conditions == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "conditions");
        }
        return new CompositeCondition(LogicalOperator.AND, List.of(conditions));
    }

    public static CompositeCondition or(MessageCondition... conditions) {
        if (conditions == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "conditions");
        }
        return new CompositeCondition(LogicalOperator.OR, List.of(conditions));
    }
}
