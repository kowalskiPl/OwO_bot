package com.owobot.commands;

public class ParameterlessCommand extends Command{
    public String command;

    public ParameterlessCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "ParameterlessCommand{" +
                "command='" + command + '\'' +
                '}';
    }

    @Override
    public Command clone() {
        return null;
    }
}
