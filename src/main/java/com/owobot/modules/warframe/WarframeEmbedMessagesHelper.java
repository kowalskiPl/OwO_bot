package com.owobot.modules.warframe;

import com.owobot.commands.Command;
import com.owobot.modules.warframe.model.RewardSearchResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarframeEmbedMessagesHelper {

    public static final List<String> searchPanelButtonsIds = Collections.unmodifiableList(prepareButtonIds());
    private static List<String> prepareButtonIds() {
        List<String> ids = new LinkedList<>();
        ids.add("warframeResult_1");
        ids.add("warframeResult_2");
        ids.add("warframeResult_3");
        ids.add("warframeResult_4");
        ids.add("warframeResult_5");
        ids.add("warframeResult_Cancel");
        return ids;
    }
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
        command.getCommandMessage().getMessage().getChannel()
                .sendMessageEmbeds(builder.build())
                .queue(message -> message.delete()
                        .queueAfter(40, TimeUnit.SECONDS),
                        new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }

    public static void createAndSendRewardSearchEmbed(List<String> possibleRewards, String searchedReward, Command command) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Possible rewards for:");
        builder.setTitle(searchedReward);
        builder .setColor(Color.BLUE);

        int iterator = 1;
        for (String reward : possibleRewards) {
            builder.addField( " ",iterator + " " + reward, false);
            iterator++;
        }

        command.getCommandMessage().getMessage().getChannel()
                .sendMessageEmbeds(builder.build())
                .setComponents(
                        ActionRow.of(Button.secondary(searchPanelButtonsIds.get(0), "1"),
                                Button.secondary(searchPanelButtonsIds.get(1), "2"),
                                Button.secondary(searchPanelButtonsIds.get(2), "3"),
                                Button.secondary(searchPanelButtonsIds.get(3), "4"),
                                Button.secondary(searchPanelButtonsIds.get(4), "5")
                        ),
                        ActionRow.of(Button.danger(searchPanelButtonsIds.get(5), "Cancel"))
                )
                .queue(message -> message.delete()
                        .queueAfter(30, TimeUnit.SECONDS),
                        new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
    }
}
