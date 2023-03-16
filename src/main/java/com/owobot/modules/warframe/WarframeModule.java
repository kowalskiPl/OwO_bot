package com.owobot.modules.warframe;

import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WarframeModule implements Module {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<CommandListener> getCommandListeners() {
        return null;
    }

    @Override
    public Set<Command> getCommands() {
        return null;
    }

    @Override
    public String getNameUserFriendly() {
        return null;
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return getName().compareTo(o.getName());
    }
}
