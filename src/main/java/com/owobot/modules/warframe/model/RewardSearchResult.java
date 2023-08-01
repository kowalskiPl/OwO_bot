package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode
public class RewardSearchResult implements Comparable<RewardSearchResult> {
    private String missionName;
    private String missionType;
    private String planet;
    private Reward rewardA;
    private Reward rewardB;
    private Reward rewardC;
    private Reward justReward;
    private boolean isRelic;
    private Reward relicReward;

    private Reward extractReward() {
        if (rewardA != null) {
            return rewardA;
        }
        if (rewardB != null) {
            return rewardB;
        }
        if (rewardC != null) {
            return rewardC;
        }
        if (justReward != null) {
            return justReward;
        }
        if (relicReward != null) {
            return relicReward;
        }
        return new Reward();
    }

    @Override
    public int compareTo(@NotNull RewardSearchResult o) {
        return this.extractReward().compareTo(o.extractReward());
    }
}
