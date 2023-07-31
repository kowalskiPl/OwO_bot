package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;

import java.util.LinkedHashMap;

public class EurobeatCommand extends PlayCommand {

    public EurobeatCommand(String parentModule) {
        super(parentModule);
        name = "Eurobeat";
        triggers.clear();
        triggers.add("Eurobeat");
        triggers.add("EurobeatKurwa");
    }

    public EurobeatCommand(PlayCommand command) {
        super(command);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
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
