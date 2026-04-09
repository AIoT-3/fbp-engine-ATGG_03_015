package com.fbp.engine.cli.command.impl;

import com.fbp.engine.cli.command.AbstractCommand;
import com.fbp.engine.engine.FlowEngine;

public class ListCommand extends AbstractCommand {
    private static final String COMMAND_NAME = "list";
    private static final String USAGE = "list";
    private static final int REQUIRED_ARGS_COUNT = 0;

    public ListCommand() {
        super(COMMAND_NAME, USAGE, REQUIRED_ARGS_COUNT);
    }

    @Override
    public boolean execute(FlowEngine flowEngine, String[] args) {
        flowEngine.listFlows();
        return true;
    }
}
