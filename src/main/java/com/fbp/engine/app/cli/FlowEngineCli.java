package com.fbp.engine.app.cli;

import com.fbp.engine.app.cli.command.Command;
import com.fbp.engine.app.cli.command.CommandFactory;
import com.fbp.engine.app.cli.parser.CommandParser;
import com.fbp.engine.app.cli.parser.ParsedCommand;
import com.fbp.engine.core.flow.runtime.FlowEngine;
import com.fbp.engine.core.exception.EngineException;

import java.util.Map;
import java.util.Scanner;

public class FlowEngineCli {
    private final FlowEngine flowEngine = new FlowEngine();
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Command> commands = CommandFactory.getCommands();
    private final CommandParser commandParser = new CommandParser();

    public static void main(String[] args) {
        new FlowEngineCli().run();
    }

    private void run() {
        boolean running = true;

        while (running) {
            System.out.print("fbp> ");
            String input = scanner.nextLine();

            try {
                ParsedCommand parsedCommand = commandParser.parse(input, commands);
                if (parsedCommand == null) {
                    continue;
                }

                running = parsedCommand.command().execute(flowEngine, parsedCommand.args());

            } catch (EngineException e) {
                System.out.println(e.getMessage());
            }
        }

        flowEngine.shutdown();
        scanner.close();
    }
}
