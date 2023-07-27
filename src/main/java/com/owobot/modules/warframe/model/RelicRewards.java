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

    public Optional<RewardSearchResult> searchReward(String rewardName) {
        if (rareReward.getName().equals(rewardName)){
            RewardSearchResult result = new RewardSearchResult();
            result.setRelic(true);
            result.setRelicReward(rareReward);
            return Optional.of(result);
        }

        var commonResult = commonRewards.stream().filter(reward -> reward.getName().equals(rewardName)).findFirst();
        if (commonResult.isPresent()){
            RewardSearchResult result = new RewardSearchResult();
            result.setRelic(true);
            result.setRelicReward(commonResult.get());
            return Optional.of(result);
        }

        var uncommonResult = uncommonRewards.stream().filter(reward -> reward.getName().equals(rewardName)).findFirst();
        if (uncommonResult.isPresent()){
            RewardSearchResult result = new RewardSearchResult();
            result.setRelic(true);
            result.setRelicReward(uncommonResult.get());
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
