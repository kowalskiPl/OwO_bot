package com.owobot.modules.warframe.commands;

import com.owobot.commands.Command;
import com.owobot.modules.warframe.WarframeParameterNames;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class SearchMissionRewardCommand extends Command {
    public SearchMissionRewardCommand(String parentModule) {
        super(parentModule);
        triggers = new LinkedHashSet<>(Set.of(
                "searchMissionReward",
                "getRewardLocations",
                "getReward",
                "findReward",
                "findRewardLocations"
        ));
        name = "searchMissionReward";
    }

    public SearchMissionRewardCommand(SearchMissionRewardCommand command) {
        super(command);
    }

    @Override
    public void setParameters(String parameters) {
        this.parameterMap = new LinkedHashMap<>();
        if (parameters != null) {
            parameterMap.put(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName(), parameters);
        }
    }

    @Override
    public Command clone() {
        return new SearchMissionRewardCommand(this);
    }

    @Override
    protected String getInfo() {
        return "Finds missions with specified relic as a reward. Displays highest drop chance missions";
    }
}
