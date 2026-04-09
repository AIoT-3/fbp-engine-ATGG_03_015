package com.fbp.engine.message;

public record PortMessage(
        String inputPortName,
        Message message
) {
}
