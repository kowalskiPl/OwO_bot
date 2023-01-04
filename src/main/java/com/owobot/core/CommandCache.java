package com.owobot.core;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.utilities.Reflectional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CommandCache extends Reflectional {

    private final Map<String, Set<Command>> commandMap;

    public CommandCache(OwoBot owoBot) {
        super(owoBot);
        commandMap = new LinkedHashMap<>();
    }

    public void loadCommand(String commandSet, Command command) {
        if (commandMap.containsKey(commandSet)) {
            commandMap.get(commandSet).add(command);
        } else {
            commandMap.put(commandSet, new HashSet<>(Set.of(command)));
        }
    }

    public void loadCommandSet(String commandSet, Set<Command> commands) {
        if (commandMap.containsKey(commandSet)) {
            commandMap.get(commandSet).addAll(commands);
        } else {
            commandMap.put(commandSet, commands);
        }
    }

    public boolean isCommandLoaded(Command command) {
        return commandMap.entrySet().stream().anyMatch(entrySet -> entrySet.getValue().contains(command));
    }

    public Command getCommand(String commandTrigger) {
        var result = commandMap.entrySet().stream().filter(entrySet -> entrySet.getValue().stream().anyMatch(entry -> entry.getTriggers().stream().anyMatch(trigger -> trigger.equals(commandTrigger)))).findFirst();
        if (result.isPresent()){
            var commandSet = result.get().getValue();
            var command = commandSet.stream().filter(entry -> entry.getTriggers().stream().anyMatch(trigger -> trigger.equals(commandTrigger))).findFirst();
            return command.get().clone();
        }
        return null;
    }
}
