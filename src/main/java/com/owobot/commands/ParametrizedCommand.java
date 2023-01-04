package com.owobot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParametrizedCommand extends Command{
    public String command;
    public List<String> parameters;

    public ParametrizedCommand(String command, String... parameters) {
        this.command = command;
        this.parameters = new ArrayList<>();
        Collections.addAll(this.parameters, parameters);
    }

    @Override
    public String toString() {
        return "ParametrizedCommand{" +
                "command='" + command + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    @Override
    public Command clone() {
        return null;
    }
}
