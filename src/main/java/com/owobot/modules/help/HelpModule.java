package com.owobot.modules.help;


import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.help.commands.GetHelpCommand;
import com.owobot.modules.help.listener.HelpCommandListener;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class HelpModule extends Reflectional implements Module {
    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;

    public HelpModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(new HelpCommandListener(owoBot)));
        commands = new LinkedHashSet<>(Set.of(new GetHelpCommand(this.getName())));
    }

    @Override
    public String getName() {
        return HelpModule.class.getName();
    }

    @Override
    public Set<CommandListener> getCommandListeners() {
        return commandListeners;
    }

    @Override
    public Set<Command> getCommands() {
        return commands;
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return o.getName().compareTo(this.getName());
    }

    @Override
    public String getNameUserFriendly() {
        return "Help";
    }
}
