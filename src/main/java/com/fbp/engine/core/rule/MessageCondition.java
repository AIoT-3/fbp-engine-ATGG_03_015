package com.fbp.engine.core.rule;

import com.fbp.engine.core.message.Message;

@FunctionalInterface
public interface MessageCondition {
    boolean matches(Message message);
}
