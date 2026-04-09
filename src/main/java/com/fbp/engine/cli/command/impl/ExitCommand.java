package com.fbp.engine.cli.command.impl;

import com.fbp.engine.cli.command.AbstractCommand;
import com.fbp.engine.engine.FlowEngine;

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
