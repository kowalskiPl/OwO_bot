package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class RewardSearchResult implements Comparable<RewardSearchResult> {
    public abstract String getRewardText();

    protected abstract Double getHighestDropChance();
}
