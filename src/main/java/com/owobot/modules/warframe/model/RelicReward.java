package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class RelicReward {
    private String relicName;
    private List<String> commonRewards;
    private List<String> uncommonRewards;
    private String rareReward;

    public boolean containsReward(String rewardName) {
        if (rareReward.equalsIgnoreCase(rewardName))
            return true;

        if (commonRewards.contains(rewardName))
            return true;

        return uncommonRewards.contains(rewardName);
    }
}
