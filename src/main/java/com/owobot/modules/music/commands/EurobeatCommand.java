package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EurobeatCommand extends PlayCommand {

    public EurobeatCommand() {
        name = "Eurobeat";
        triggers.clear();
        triggers.add("Eurobeat");
        triggers.add("EurobeatKurwa");
    }

    public EurobeatCommand(PlayCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new EurobeatCommand(this);
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_SONG_NAME.getName(), "https://www.youtube.com/watch?v=ZqV2h2Nb3To&list=PL20VXGg6LmYRLK9OFcpa8SvVsyN-nZj0L&index=1");
        parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_IS_SONG_URL.getName(), "true");
    }

    @Override
    public List<String> getMiddlewares() {
        List<String> middlewares = new ArrayList<>();
        middlewares.add("musicChannel");
        return middlewares;
    }

    @Override
    protected String getInfo() {
        return "Plays fooken Eurobeat god damn!";
    }

    @Override
    public String toString() {
        return "EurobeatCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
