package com.owobot.modules.admin.commands;

import com.owobot.commands.Command;

import java.util.LinkedHashSet;
import java.util.Set;

public class ListPrefixesCommand extends Command {
    public ListPrefixesCommand() {
        name = "listPrefixes";
        triggers = new LinkedHashSet<>(Set.of(
                "listPrefixes",
                "getPrefixes",
                "showPrefixes"
        ));
    }

    protected ListPrefixesCommand(ListPrefixesCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new ListPrefixesCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Lists currently active prefixes for the server";
    }

    @Override
    public void setParameters(String parameters) {
        super.setParameters(parameters);
    }

    @Override
    public String toString() {
        return "ListPrefixesCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
