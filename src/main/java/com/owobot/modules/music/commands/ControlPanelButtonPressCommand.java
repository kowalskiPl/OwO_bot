package com.owobot.modules.music.commands;

import com.owobot.commands.Command;
import com.owobot.modules.music.MusicParameterNames;
import com.owobot.modules.music.PlayerControlPanel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class ControlPanelButtonPressCommand extends Command {

    public ControlPanelButtonPressCommand(String parentModule) {
        super(parentModule);
        name = "controlPanelButtonPress";
        triggers = new LinkedHashSet<>();
        var ids = PlayerControlPanel.controlPanelButtonsIds;
        triggers.addAll(ids);
        isButtonCommand = true;
    }

    public ControlPanelButtonPressCommand(ControlPanelButtonPressCommand command) {
        super(command);
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        parameterMap.put(MusicParameterNames.MUSIC_PARAMETER_BUTTON_ID.getName(), parameters);
    }

    @Override
    public Command clone() {
        return new ControlPanelButtonPressCommand(this);
    }

    @Override
    protected String getInfo() {
        return "This should not be here";
    }

    @Override
    public String toString() {
        return "ControlPanelButtonPressCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
