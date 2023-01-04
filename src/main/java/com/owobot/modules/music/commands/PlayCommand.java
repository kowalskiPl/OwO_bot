package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;
import org.apache.commons.validator.routines.UrlValidator;

public class PlayCommand extends Command {
    public PlayCommand() {
        super();
        name = "play";
        triggers.add("play");
        triggers.add("start");
    }

    @Override
    public void setParameters(String parameters) {
        if (UrlValidator.getInstance().isValid(parameters)){
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_SONG_URL.getName(), parameters);
        } else {
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_SONG_NAME.getName(), parameters);
        }
    }

    public PlayCommand(PlayCommand target) {
        super(target);
    }

    @Override
    public Command clone() {
        return new PlayCommand(this);
    }
}
