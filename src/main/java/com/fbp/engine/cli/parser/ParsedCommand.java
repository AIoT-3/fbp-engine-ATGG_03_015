package com.fbp.engine.cli.parser;

import com.fbp.engine.cli.command.Command;

public record ParsedCommand(
        Command command,
        String[] args
) {
}
