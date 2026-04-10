package com.fbp.engine.cli.command;

import com.fbp.engine.cli.command.impl.ExitCommand;
import com.fbp.engine.cli.command.impl.ListCommand;
import com.fbp.engine.cli.command.impl.StartCommand;
import com.fbp.engine.cli.command.impl.StopCommand;

import java.util.Map;

public class CommandFactory {
    private CommandFactory() {
        /* This utility class should not be instantiated */
    }

    public static Map<String, Command> getCommands() {
        Command startCommand = new StartCommand();
        Command stopCommand = new StopCommand();
        Command listCommand = new ListCommand();
        Command exitCommand = new ExitCommand();

        return Map.of(
                startCommand.getName(), startCommand,
                stopCommand.getName(), stopCommand,
                listCommand.getName(), listCommand,
                exitCommand.getName(), exitCommand
        );
    }
}
