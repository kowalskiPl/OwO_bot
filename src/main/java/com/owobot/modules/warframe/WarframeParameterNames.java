package com.owobot.modules.warframe;

public enum WarframeParameterNames {

    WARFRAME_PARAMETER_SEARCH_BUTTON_ID("searchButtonId"),
    WARFRAME_PARAMETER_RELIC_NAME("relicName"),
    WARFRAME_PARAMETER_REWARD_SEARCH_STRING("rewardSearchString");

    private final String name;
    WarframeParameterNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
