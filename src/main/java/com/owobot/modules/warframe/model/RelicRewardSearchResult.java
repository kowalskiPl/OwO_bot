package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RelicRewardSearchResult extends RewardSearchResult {
    private Reward relicReward;
    private String relicName;

    @Override
    public String getRewardText() {
        StringBuilder sb = new StringBuilder();
        sb.append(relicName).append(", Rarity: ").append(relicReward.getRarity()).append(", Drop chance: ").append(relicReward.getDropChance()).append("%");
        return sb.toString();
    }

    @Override
    protected Double getHighestDropChance() {
        return relicReward.getDropChance();
    }

    @Override
    public int compareTo(@NotNull RewardSearchResult o) {
        return 0;
    }
}
