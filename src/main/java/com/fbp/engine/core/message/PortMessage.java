package com.fbp.engine.core.message;

public record PortMessage(
        String inputPortName,
        Message message
) {
}
