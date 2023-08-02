package com.owobot.modules.warframe.commands;

import com.owobot.commands.Command;
import com.owobot.modules.warframe.WarframeEmbedMessagesManager;
import com.owobot.modules.warframe.WarframeParameterNames;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class WarframeSearchButtonPressCommand extends Command {

    public WarframeSearchButtonPressCommand(String parentModule) {
        super(parentModule);
        name = "warframeSearchButtonPress";
        triggers = new LinkedHashSet<>();
        var ids = WarframeEmbedMessagesManager.searchPanelButtonsIds;
        triggers.addAll(ids);
        isButtonCommand = true;
    }

    public WarframeSearchButtonPressCommand(WarframeSearchButtonPressCommand command) {
        super(command);
    }

    @Override
    public void setParameters(String parameters) {
        parameterMap = new LinkedHashMap<>();
        parameterMap.put(WarframeParameterNames.WARFRAME_PARAMETER_SEARCH_BUTTON_ID.getName(), parameters);
    }

    @Override
    public Command clone() {
        return new WarframeSearchButtonPressCommand(this);
    }

    @Override
    protected String getInfo() {
        return null;
    }

    @Override
    public String toString() {
        return "WarframeSearchButtonPressCommand{" +
                "name='" + name + '\'' +
                ", triggers=" + triggers +
                ", commandMessage=" + commandMessage +
                ", parameterMap=" + parameterMap +
                ", isButtonCommand=" + isButtonCommand +
                '}';
    }
}
