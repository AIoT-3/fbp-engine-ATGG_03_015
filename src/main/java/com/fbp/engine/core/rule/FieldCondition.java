package com.fbp.engine.core.rule;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.Message;

public record FieldCondition (
        String fieldName,
        ComparisonOperator operator,
        Object expectedValue
) implements MessageCondition {

    public FieldCondition {
        if (fieldName == null || fieldName.isBlank()) {
            throw new EngineException(EngineFailureType.RULE_FIELD_NAME_INVALID, fieldName);
        }
        if (operator == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "operator");
        }
        if (expectedValue == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "expectedValue");
        }
    }

    @Override
    public boolean matches(Message message) {
        Object actualValue = message.get(fieldName);
        if (actualValue == null) {
            return false;
        }

        // 숫자 비교
        if (actualValue instanceof Number actualNumber
                && expectedValue instanceof Number expectedNumber
        ) {
            return operator.compare(
                    actualNumber.doubleValue(),
                    expectedNumber.doubleValue());
        }

        // 문자열 비교
        if (operator == ComparisonOperator.EQ) {
            return actualValue.equals(expectedValue);
        }
        if (operator == ComparisonOperator.NE) {
            return !actualValue.equals(expectedValue);
        }

        return false;
    }
}
