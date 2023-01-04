package com.owobot.commands;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class Command{
    protected String name; //has to be unique
    protected Set<String> triggers; //all have to be unique
    protected CommandMessage commandMessage;
    protected Map<String, String> parameterMap;

    protected Command() {
        name = "";
        triggers = new LinkedHashSet<>();
        commandMessage = null;
    }

    protected Command(Command command){
        this.name = command.getName();
        this.triggers = command.getTriggers();
        this.commandMessage = command.commandMessage;
    }

    public String getName() {
        return name;
    }

    public Set<String> getTriggers() {
        return triggers;
    }

    public void setCommandMessage(CommandMessage commandMessage) {
        this.commandMessage = commandMessage;
    }

    public CommandMessage getCommandMessage() {
        return commandMessage;
    }

    public void setParameters(String parameters){

    }
    public Map<String, String> getParameterMap(){
        return parameterMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return name.equals(command.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public abstract Command clone();

    public String getHelp(){
        return "A generic command class, nothing to see here";
    }
}
