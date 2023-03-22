package com.owobot.modules.warframe;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.warframe.commands.SearchMissionRewardCommand;
import com.owobot.modules.warframe.listener.WarframeCommandListener;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class WarframeModule extends Reflectional implements Module {
    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;
    @Override
    public String getName() {
        return WarframeModule.class.getName();
    }

    public WarframeModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(new WarframeCommandListener(owoBot)));
        commands = new LinkedHashSet<>(Set.of(
                new SearchMissionRewardCommand()
        ));
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
        return "Warframe";
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return getName().compareTo(o.getName());
    }
}
