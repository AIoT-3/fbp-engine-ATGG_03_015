package com.fbp.engine.edge.runtime;

import com.fbp.engine.edge.Edge;
import com.fbp.engine.port.InputPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WireRuntime {
    private final Edge edge;
    private final InputPort targetInputPort;

    public void dispatch() {
        targetInputPort.receive(edge.connection().poll());
    }
}
