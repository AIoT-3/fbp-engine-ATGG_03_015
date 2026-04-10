package com.fbp.engine.app.cli.command.impl;

import com.fbp.engine.app.cli.command.AbstractCommand;
import com.fbp.engine.core.flow.runtime.FlowEngine;

public class ExitCommand extends AbstractCommand {
    private static final String COMMAND_NAME = "exit";
    private static final String USAGE = "exit";
    private static final int REQUIRED_ARGS_COUNT = 0;

    public ExitCommand() {
        super(COMMAND_NAME, USAGE, REQUIRED_ARGS_COUNT);
    }

    @Override
    public boolean execute(FlowEngine flowEngine, String[] args) {
        return false;
    }
}
