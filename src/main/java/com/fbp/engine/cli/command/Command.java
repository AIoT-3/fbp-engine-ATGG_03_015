package com.fbp.engine.cli.command;

import com.fbp.engine.engine.FlowEngine;

public interface Command {
    String getName();
    String getUsage();
    int getRequiredArgsCount();
    boolean execute(FlowEngine flowEngine, String[] args);
}
