package com.owobot.commands;

public class EmptyCommand extends Command {

    public EmptyCommand() {
        super();
    }

    @Override
    public String toString() {
        return "EmptyCommand{ Nothing HERE }";
    }

    @Override
    public Command clone() {
        return null;
    }

    @Override
    protected String getInfo() {
        return "Nothing to see here";
    }

}
