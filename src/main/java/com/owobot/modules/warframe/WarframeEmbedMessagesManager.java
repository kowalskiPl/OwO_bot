package com.owobot.modules.warframe;

import com.owobot.commands.Command;
import com.owobot.modules.warframe.model.RewardSearchResult;
import com.owobot.utilities.Pair;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WarframeEmbedMessagesManager {

    public static final List<String> searchPanelButtonsIds = Collections.unmodifiableList(prepareButtonIds());

    private final Map<Long, MessageChannel> currentLongLivingEmbeds = new LinkedHashMap<>();

    private static List<String> prepareButtonIds() {
        List<String> ids = new ArrayList<>();
        ids.add("warframeResult_1");
        ids.add("warframeResult_2");
        ids.add("warframeResult_3");
        ids.add("warframeResult_4");
        ids.add("warframeResult_5");
        ids.add("warframeResult_Cancel");
        return ids;
    }

    private synchronized void addLongLivingEmbed(MessageChannel messageChannel, Long id) {
        currentLongLivingEmbeds.put(id, messageChannel);
    }

    private synchronized void removeLongLivingEmbed(Long id) {
        currentLongLivingEmbeds.remove(id);
    }

    private synchronized MessageChannel getLongLivingEmbedChannel(Long id) {
        return currentLongLivingEmbeds.get(id);
    }

    private static final Map<Long, Pair<MessageChannel, Long>> userToEmbedMap = new LinkedHashMap<>();

    private synchronized void insertNewUserEmbedToMap(Long userId, MessageChannel channel, Long embedId) {
        userToEmbedMap.put(userId, new Pair<>(channel, embedId));
    }

    private synchronized Pair<MessageChannel, Long> getEmbedByUserId(Long userID) {
        return userToEmbedMap.getOrDefault(userID, null);
    }

    private synchronized void removeUserEmbedMapping(Long userId) {
        userToEmbedMap.remove(userId);
    }

    public void createAndSendRewardsEmbed(List<RewardSearchResult> rewards, String requestedReward, Command command) {
        StringBuilder body = new StringBuilder();
        body.append("**");
        for (int i = 0; i < rewards.size() && i < 10; i++) {
            body.append(i + 1).append(") ").append(rewards.get(i).getRewardText()).append(System.lineSeparator());
        }
        body.append("**");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Best loot sources for:")
                .setTitle(requestedReward)
                .setDescription(body.toString())
                .setTimestamp(command.getCommandMessage().getMessage().getTimeCreated())
                .setColor(Color.BLUE);

        if (command.getCommandMessage().isGuildMessage()) {
            builder.setFooter("This message will expire in 2 minutes");
            command.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).queue(message -> {
                addLongLivingEmbed(message.getChannel(), message.getIdLong());
                message.delete().queueAfter(120, TimeUnit.SECONDS, msg -> removeLongLivingEmbed(message.getIdLong()), new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            });
        } else {
            command.getCommandMessage().getMessage().getChannel().sendMessageEmbeds(builder.build()).queue();
        }
    }

    public void createAndSendRewardSearchEmbed(List<String> possibleRewards, String searchedReward, Command command, Map<Long, Map<String, String>> userSearchResults) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Possible rewards for:");
        builder.setTitle(searchedReward);
        builder.setColor(Color.BLUE);
        StringBuilder sb = new StringBuilder();

        sb.append("**");
        int iterator = 1;
        for (String reward : possibleRewards) {
            sb.append(iterator).append(") ").append(reward).append(System.lineSeparator());
            iterator++;
        }
        sb.append("**");
        var rewardEmbedBody = sb.toString().trim();
        builder.setDescription(rewardEmbedBody);
        builder.setFooter("This message will expire in 40s");
        builder.setTimestamp(command.getCommandMessage().getMessage().getTimeCreated());

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
                .queue(message -> {
                    insertNewUserEmbedToMap(command.getCommandMessage().getUser().getIdLong(), message.getChannel(), message.getIdLong());
                    message.delete()
                            .queueAfter(40, TimeUnit.SECONDS,
                                    deleteMessage -> {
                                synchronized (userSearchResults){
                                    userSearchResults.remove(command.getCommandMessage().getUser().getIdLong());
                                }
                                removeUserEmbedMapping(command.getCommandMessage().getUser().getIdLong())
                                ;},
                                    new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));

                });
    }

    public void removeUserEmbed(Long userId) {
        var channel = getEmbedByUserId(userId).getKey();

        if (channel == null)
            return;

        channel.deleteMessageById(getEmbedByUserId(userId).getValue()).queue();
    }

    public synchronized void killLongLivingEmbeds() {
        currentLongLivingEmbeds.forEach((id, messageChannel) -> messageChannel.deleteMessageById(id).queue());
    }
}
