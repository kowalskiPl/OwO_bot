package com.owobot.modules.admin;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.admin.commands.*;
import com.owobot.modules.admin.listener.AdminCommandListener;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class AdminModule extends Reflectional implements Module {
    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;

    public AdminModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(new AdminCommandListener(owoBot)));
        commands = new LinkedHashSet<>(Set.of(
                new AddPrefixCommand(),
                new RemovePrefixCommand(),
                new ListPrefixesCommand(),
                new EnableMusicChannelCommand(),
                new AddMusicChannelCommand(),
                new RemoveMusicChannelCommand(),
                new GetMusicChannelsCommand()
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
        return "Admin";
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
