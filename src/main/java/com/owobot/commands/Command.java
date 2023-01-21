package com.owobot.commands;

import java.util.*;

public abstract class Command{
    protected String name; //has to be unique
    protected Set<String> triggers; //all have to be unique
    protected CommandMessage commandMessage;
    protected Map<String, String> parameterMap;
    protected boolean isButtonCommand;

    protected Command() {
        name = "";
        triggers = new LinkedHashSet<>();
        commandMessage = null;
        isButtonCommand = false;
    }

    protected Command(Command command){
        this.name = command.getName();
        this.triggers = command.getTriggers();
        this.commandMessage = command.commandMessage;
        this.isButtonCommand = command.isButtonCommand;
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

    public boolean isButtonCommand() {
        return isButtonCommand;
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
        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("Triggered by:");
        triggers.forEach(trigger -> helpMessage.append(" ").append(trigger).append(","));
        helpMessage.append("\n").append(getInfo());
        return helpMessage.toString();
    }
    protected abstract String getInfo();

    public List<String> getMiddlewares(){
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
