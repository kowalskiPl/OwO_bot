package com.owobot.modules;

import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;

import java.util.Set;

public interface Module extends Comparable<Module> {

    public String getName();

    public Set<CommandListener> getCommandListeners();

    public Set<Command> getCommands();

}
