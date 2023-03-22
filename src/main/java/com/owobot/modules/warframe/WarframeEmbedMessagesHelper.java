package com.owobot.modules.warframe;

import com.owobot.commands.Command;
import com.owobot.modules.warframe.model.RewardSearchResult;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WarframeEmbedMessagesHelper {
    public static void createAndSendMissionRewardsEmbed(Set<RewardSearchResult> rewards, String requestedReward, Command command) {
        List<RewardSearchResult> sortedAndReduced = new LinkedList<>();
        try {
            sortedAndReduced = rewards.stream().sorted(
                            Comparator.nullsLast(
                                    Comparator.comparing(RewardSearchResult::getRewardA, Comparator.nullsLast(Comparator.reverseOrder()))
                                            .thenComparing(RewardSearchResult::getRewardB, Comparator.nullsLast(Comparator.reverseOrder()))
                                            .thenComparing(RewardSearchResult::getRewardC, Comparator.nullsLast(Comparator.reverseOrder()))))
                    .limit(7).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Best loot locations for:");
        builder.setTitle(requestedReward);

        sortedAndReduced.forEach(reward -> {
            builder.addField("Location", reward.getPlanet() + " " + reward.getMissionName(), true);
            builder.addField("Mission type", reward.getMissionType(), true);
            String rotations = reward.getRewardA() == null ? "" : "A : " + reward.getRewardA().getDropChance() + "%";
            rotations += reward.getRewardB() == null ? "" : "\nB : " + reward.getRewardB().getDropChance() + "%";
            rotations += reward.getRewardC() == null ? "" : "\nC : " + reward.getRewardC().getDropChance() + "%";
            builder.addField("Rotations and chance", rotations, true);
        });
        command.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(40, TimeUnit.SECONDS));
    }
}
