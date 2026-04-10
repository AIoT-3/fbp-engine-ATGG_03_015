package com.fbp.engine.app.cli.parser;

import com.fbp.engine.app.cli.command.Command;

public record ParsedCommand(
        Command command,
        String[] args
) {
}
