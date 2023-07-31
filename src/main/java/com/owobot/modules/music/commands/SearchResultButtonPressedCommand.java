package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;
import com.owobot.modules.music.listener.MusicCommandListener;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class SearchResultButtonPressedCommand extends Command {

    public SearchResultButtonPressedCommand(String parentModule) {
        super(parentModule);
        name = "searchResultButtonPressed";
        triggers = new LinkedHashSet<>();
        var buttonIdMap = MusicCommandListener.trackButtonsIds;
        for (var entry : buttonIdMap.entrySet()){
            triggers.add(entry.getKey());
        }
        isButtonCommand = true;
    }

    public SearchResultButtonPressedCommand(SearchResultButtonPressedCommand command) {
        super(command);
    }

    @Override
    public Command clone() {
        return new SearchResultButtonPressedCommand(this);
    }

    @Override
    protected String getInfo() {
        return "This should not be here";
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_BUTTON_ID.getName(), parameters);
    }

    @Override
    public String toString() {
        return "SearchResultButtonPressedCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
