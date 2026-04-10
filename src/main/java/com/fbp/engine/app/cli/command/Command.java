package com.fbp.engine.app.cli.command;

import com.fbp.engine.core.flow.runtime.FlowEngine;

public interface Command {
    String getName();
    String getUsage();
    int getRequiredArgsCount();
    boolean execute(FlowEngine flowEngine, String[] args);
}
