package com.owobot.modules.warframe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class RelicRewards {
    private String relicName;
    private List<Reward> commonRewards;
    private List<Reward> uncommonRewards;
    private Reward rareReward;

    public boolean containsReward(String rewardName) {
        if (rareReward.getName().equals(rewardName))
            return true;

        if (commonRewards.stream().anyMatch(reward -> reward.getName().equals(rewardName)))
            return true;

        return uncommonRewards.stream().anyMatch(reward -> reward.getName().equals(rewardName));
    }
}
