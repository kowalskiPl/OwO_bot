package com.owobot.modules.botadmin;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.botadmin.commands.ShutdownCommand;
import com.owobot.modules.botadmin.commands.StopShutdownCommand;
import com.owobot.modules.botadmin.listener.BotAdminCommandListener;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class BotAdminModule extends Reflectional implements Module {
    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;
    public BotAdminModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(new BotAdminCommandListener(owoBot)));
        commands = new LinkedHashSet<>(Set.of(
                new ShutdownCommand(this.getName()),
                new StopShutdownCommand(this.getName())
        ));
    }

    @Override
    public String getName() {
        return this.getClass().getName();
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
    public String getNameUserFriendly() {
        return "BotAdmin";
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return getName().compareTo(o.getName());
    }

    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        var module = (Module) obj;
        return Objects.equals(module.getName(), this.getName());
    }
}
