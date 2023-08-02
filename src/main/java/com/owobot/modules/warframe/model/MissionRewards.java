package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@RequiredArgsConstructor
public class MissionRewards {
    private String missionName;
    private String missionType;
    private String planet;
    private String extraFeatures;
    private List<Reward> rotationARewards = new ArrayList<>();
    private List<Reward> rotationBRewards = new ArrayList<>();
    private List<Reward> rotationCRewards = new ArrayList<>();
    private List<Reward> justRewards = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionRewards that = (MissionRewards) o;
        return missionName.equals(that.missionName) && missionType.equals(that.missionType) && planet.equals(that.planet) && Objects.equals(rotationARewards, that.rotationARewards) && Objects.equals(rotationBRewards, that.rotationBRewards) && Objects.equals(rotationCRewards, that.rotationCRewards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionName, missionType, planet, rotationARewards, rotationBRewards, rotationCRewards);
    }

    public Optional<MissionRewardSearchResult> searchReward(String rewardName) {
        MissionRewardSearchResult result = new MissionRewardSearchResult();
        Optional<Reward> rewardA = rotationARewards.stream().filter(reward -> StringUtils.containsIgnoreCase(reward.getName(), rewardName)).findFirst();
        Optional<Reward> rewardB = rotationBRewards.stream().filter(reward -> StringUtils.containsIgnoreCase(reward.getName(), rewardName)).findFirst();
        Optional<Reward> rewardC = rotationCRewards.stream().filter(reward -> StringUtils.containsIgnoreCase(reward.getName(), rewardName)).findFirst();
        Optional<Reward> justReward = justRewards.stream().filter(reward -> StringUtils.containsIgnoreCase(reward.getName(), rewardName)).findFirst();
        boolean found = false;

        if (rewardA.isPresent()){
            result.setRewardA(rewardA.get());
            found = true;
        }

        if (rewardB.isPresent()){
            result.setRewardB(rewardB.get());
            found = true;
        }

        if (rewardC.isPresent()){
            result.setRewardC(rewardC.get());
            found = true;
        }

        if (justReward.isPresent()){
            result.setJustReward(justReward.get());
            found = true;
        }

        if (!found)
            return Optional.empty();
        else {
            result.setMissionName(this.missionName);
            result.setPlanet(this.planet);
            result.setMissionType(this.missionType);
        }

        return Optional.of(result);
    }
}
