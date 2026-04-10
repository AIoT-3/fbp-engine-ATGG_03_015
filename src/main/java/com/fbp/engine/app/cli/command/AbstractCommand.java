package com.fbp.engine.app.cli.command;

public abstract class AbstractCommand implements Command {
    private final String name;
    private final String usage;
    private final int requiredArgsCount;

    protected AbstractCommand(String name, String usage, int requiredArgsCount) {
        this.name = name;
        this.usage = usage;
        this.requiredArgsCount = requiredArgsCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public int getRequiredArgsCount() {
        return requiredArgsCount;
    }
}
