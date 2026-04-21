package com.fbp.engine.core.node.builtin.processor;

import com.fbp.engine.core.exception.EngineException;
import com.fbp.engine.core.exception.EngineFailureType;
import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.model.AbstractNode;
import com.fbp.engine.core.rule.MessageCondition;

public class RuleNode extends AbstractNode {
    private final MessageCondition condition;

    public RuleNode(String id, MessageCondition condition) {
        super(id);
        if (condition == null) {
            throw new EngineException(EngineFailureType.RULE_DEFINITION_FIELD_REQUIRED, "condition");
        }
        this.condition = condition;
        addInputPort("in");
        addOutputPort("match");
        addOutputPort("mismatch");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        if (condition.matches(portMessage.message())) {
            send("match", portMessage.message());
        } else {
            send("mismatch", portMessage.message());
        }
    }
}
