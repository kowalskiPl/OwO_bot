package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PlayCommand extends Command {
    public PlayCommand() {
        super();
        name = "play";
        triggers.add("play");
        triggers.add("start");
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        if (UrlValidator.getInstance().isValid(parameters)){
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_SONG_NAME.getName(), parameters);
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_IS_SONG_URL.getName(), "true");
        } else {
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_SONG_NAME.getName(), parameters);
            parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_IS_SONG_URL.getName(), "false");
        }
    }

    public PlayCommand(PlayCommand target) {
        super(target);
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("musicChannel");
        middlewares.add("guildChannel");
        return middlewares;
    }

    @Override
    public Command clone() {
        return new PlayCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Makes the bot join the chat and play music. Accepts one parameter, a link to YT video or playlist or a search query";
    }

    @Override
    public String toString() {
        return "PlayCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
