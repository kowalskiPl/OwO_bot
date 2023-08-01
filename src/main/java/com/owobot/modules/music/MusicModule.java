package com.owobot.modules.music;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.Module;
import com.owobot.modules.music.commands.*;
import com.owobot.modules.music.listener.MusicCommandListener;
import com.owobot.utilities.Reflectional;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class MusicModule extends Reflectional implements Module {

    //Eurobeat kurwa https://www.youtube.com/watch?v=ZqV2h2Nb3To&list=PL20VXGg6LmYRLK9OFcpa8SvVsyN-nZj0L&index=1

    private final Set<CommandListener> commandListeners;
    private final Set<Command> commands;

    public MusicModule(OwoBot owoBot) {
        super(owoBot);
        commandListeners = new LinkedHashSet<>(Set.of(
                new MusicCommandListener(owoBot)
        ));

        commands = new LinkedHashSet<>(Set.of(
                new PlayCommand(this.getName()),
                new StopCommand(this.getName()),
                new LeaveCommand(this.getName()),
                new PauseCommand(this.getName()),
                new SearchResultButtonPressedCommand(this.getName()),
                new ControlPanelButtonPressCommand(this.getName()),
                new EurobeatCommand(this.getName())
        ));
    }

    @Override
    public String getName() {
        return "Music module";
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
        return "Music";
    }

    @Override
    public int compareTo(@NotNull Module o) {
        return o.getName().compareTo(this.getName());
    }

    @Override
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
