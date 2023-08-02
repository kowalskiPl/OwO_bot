package com.owobot.modules.warframe.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class MissionRewardSearchResult extends RewardSearchResult {
    private String missionName;
    private String missionType;
    private String planet;
    private Reward rewardA;
    private Reward rewardB;
    private Reward rewardC;
    private Reward justReward;

    @Override
    public String getRewardText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Planet: ").append(planet).append(", Mission: ").append(missionType).append(" Type: ").append(missionType);
        if (justReward != null){
            sb.append(", Drop chance: ").append(justReward.getDropChance()).append("%");
        } else {
            if (rewardA != null){
                sb.append(", Rotation A drop chance: ").append(rewardA.getDropChance()).append("%");
            }

            if (rewardB != null){
                sb.append(", Rotation B drop chance: ").append(rewardB.getDropChance()).append("%");
            }

            if (rewardC != null){
                sb.append(", Rotation C drop chance: ").append(rewardC.getDropChance()).append("%");
            }
        }
        return sb.toString();
    }

    @Override
    public int compareTo(@NotNull RewardSearchResult o) {
        return Double.compare(this.getHighestDropChance(), o.getHighestDropChance());
    }

    @Override
    protected Double getHighestDropChance() {
        var currentHighestDropChance = 0d;
        if (justReward != null){
            return justReward.getDropChance();
        } else {
            if (rewardA != null){
                currentHighestDropChance = Math.max(rewardA.getDropChance(), currentHighestDropChance);
            }

            if (rewardB != null){
                currentHighestDropChance = Math.max(rewardB.getDropChance(), currentHighestDropChance);
            }

            if (rewardC != null){
                currentHighestDropChance = Math.max(rewardC.getDropChance(), currentHighestDropChance);
            }
        }
        return currentHighestDropChance;
    }
}
