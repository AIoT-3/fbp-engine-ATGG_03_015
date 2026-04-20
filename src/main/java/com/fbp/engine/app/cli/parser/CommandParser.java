package com.fbp.engine.app.cli.parser;

import com.fbp.engine.app.cli.command.Command;
import com.fbp.engine.app.cli.exception.CliException;
import com.fbp.engine.app.cli.exception.CliFailureType;

import java.util.Arrays;
import java.util.Map;

public class CommandParser {

    public ParsedCommand parse(String input, Map<String, Command> commands) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String[] tokens = input.trim().split("\\s+");
        Command command = commands.get(tokens[0]);
        if (command == null) {
            throw new CliException(CliFailureType.COMMAND_NOT_FOUND, tokens[0]);
        }

        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
        if (args.length < command.getRequiredArgsCount()) {
            throw new CliException(CliFailureType.INVALID_COMMAND_ARGUMENTS, command.getUsage());
        }

        return new ParsedCommand(command, args);
    }
}
