package com.fbp.engine.app.cli.command.impl;

import com.fbp.engine.app.cli.command.AbstractCommand;
import com.fbp.engine.core.flow.runtime.FlowEngine;

public class StopCommand extends AbstractCommand {
    private static final String COMMAND_NAME = "stop";
    private static final String USAGE = "stop <flowId>";
    private static final int REQUIRED_ARGS_COUNT = 1;

    public StopCommand() {
        super(COMMAND_NAME, USAGE, REQUIRED_ARGS_COUNT);
    }

    @Override
    public boolean execute(FlowEngine flowEngine, String[] args) {
        flowEngine.stopFlow(args[0]);
        return true;
    }
}
