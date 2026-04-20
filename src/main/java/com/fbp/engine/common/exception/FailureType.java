package com.fbp.engine.common.exception;

import java.io.Serializable;

public interface FailureType extends Serializable {
    String getMessageTemplate();

    default String formatMessage(Object... args) {
        if (args == null || args.length == 0) {
            return getMessageTemplate();
        }

        return getMessageTemplate().formatted(args);
    }
}
