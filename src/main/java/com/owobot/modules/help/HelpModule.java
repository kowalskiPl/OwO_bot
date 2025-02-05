package com.owobot.modules.help;


import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.help.commands.GetHelpCommand;
import com.owobot.modules.help.commands.HelpSlashCommands;
import com.owobot.modules.help.listener.HelpCommandListener;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class HelpModule extends Reflectional implements Module {
    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;
    private final HelpSlashCommands slashCommands;

    public HelpModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(new HelpCommandListener(owoBot)));
        commands = new LinkedHashSet<>(Set.of(new GetHelpCommand(this.getName())));
        slashCommands = new HelpSlashCommands();
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
    public Set<SlashCommandData> getGlobalSlashCommands() {
        return slashCommands.getGlobalCommands();
    }

    @Override
    public Set<SlashCommandData> getGuildSlashCommands() {
        return slashCommands.getGuildCommands();
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
