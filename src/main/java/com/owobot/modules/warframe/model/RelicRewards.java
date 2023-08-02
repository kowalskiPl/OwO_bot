package com.owobot.modules.warframe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

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

    public Optional<RelicRewardSearchResult> searchReward(String rewardName) {
        if (rareReward.getName().equals(rewardName)){
            RelicRewardSearchResult result = new RelicRewardSearchResult();
            result.setRelicName(relicName);
            result.setRelicReward(rareReward);
            return Optional.of(result);
        }

        var commonResult = commonRewards.stream().filter(reward -> reward.getName().equals(rewardName)).findFirst();
        if (commonResult.isPresent()){
            RelicRewardSearchResult result = new RelicRewardSearchResult();
            result.setRelicReward(commonResult.get());
            result.setRelicName(relicName);
            return Optional.of(result);
        }

        var uncommonResult = uncommonRewards.stream().filter(reward -> reward.getName().equals(rewardName)).findFirst();
        if (uncommonResult.isPresent()){
            RelicRewardSearchResult result = new RelicRewardSearchResult();
            result.setRelicReward(uncommonResult.get());
            result.setRelicName(relicName);
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
