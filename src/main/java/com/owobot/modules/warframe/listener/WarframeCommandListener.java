package com.owobot.modules.warframe.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.model.TrigramSearchResult;
import com.owobot.modules.warframe.AllRewardsDatabase;
import com.owobot.modules.warframe.WarframeEmbedMessagesHelper;
import com.owobot.modules.warframe.WarframeParameterNames;
import com.owobot.modules.warframe.commands.SearchMissionRewardCommand;
import com.owobot.modules.warframe.commands.WarframeSearchButtonPressCommand;
import com.owobot.modules.warframe.model.RewardSearchResult;
import com.owobot.utilities.Reflectional;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WarframeCommandListener extends Reflectional implements CommandListener {
    private final AllRewardsDatabase rewardsDatabase;
    private final int searchCutOffPercentage = 50;
    private static final Logger log = LoggerFactory.getLogger(WarframeCommandListener.class);
    private static final Map<Long, Map<String, String>> guildSearchResults = new LinkedHashMap<>();


    public WarframeCommandListener(OwoBot owoBot) {
        super(owoBot);
        try {
            rewardsDatabase = new AllRewardsDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void updateSearchResults(Long guildId, Map<String, String> newResults) {
        if (!guildSearchResults.containsKey(guildId)) {
            guildSearchResults.put(guildId, newResults);
        } else {
            guildSearchResults.get(guildId).clear();
            guildSearchResults.get(guildId).putAll(newResults);
        }
    }

    private synchronized Map<String, String> getSearchResults(Long guildId) {
        return guildSearchResults.getOrDefault(guildId, Collections.emptyMap());
    }

    @Override
    public boolean onCommand(Command command) {
        if (command instanceof SearchMissionRewardCommand relicRewardCommand) {
            return handleMissionRewardSearchCommand(relicRewardCommand);
        }

        if (command instanceof WarframeSearchButtonPressCommand searchCommand) {
            return handleSearchButtonPressCommand(searchCommand);
        }

        return false;
    }

    private boolean handleMissionRewardSearchCommand(SearchMissionRewardCommand command) {
        if (command.getParameterMap().containsKey(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName())) {
            return handleSearchMissionRewardCommand2(command);
        } else {
            command.getCommandMessage().getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
            return false;
        }
    }

    private boolean handleSearchMissionRewardCommand2(SearchMissionRewardCommand command) {
        var searchedReward = command.getParameterMap().get(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName());
        var searchResults = rewardsDatabase.searchAllRewards(searchedReward, 5);
        var listOfResults = searchResults.stream().map(ExtractedResult::getString).toList();

        Map<String, String> resultsMappedToIds = IntStream.range(0, listOfResults.size())
                .boxed()
                .collect(Collectors.toMap(WarframeEmbedMessagesHelper.searchPanelButtonsIds::get, listOfResults::get));

        updateSearchResults(command.getCommandMessage().getGuild().getIdLong(), resultsMappedToIds);

        var directMatch = searchResults.stream().max(Comparator.comparing(ExtractedResult::getScore)).stream().findFirst();
        boolean directlyMatched = false;
        if (directMatch.isPresent()){
            if (directMatch.get().getScore() == 100){
                directlyMatched = true;
                // TODO: Add processing of direct match
                command.getCommandMessage().getMessageChannel().sendMessage("Wow it's a direct match!").queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            }
        }

        if (!directlyMatched && !searchResults.isEmpty()) {
            WarframeEmbedMessagesHelper.createAndSendRewardSearchEmbed(listOfResults, searchedReward, command);
        }
        command.getCommandMessage().getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
        return true;
    }

    private boolean handleSearchButtonPressCommand(WarframeSearchButtonPressCommand command) {
        var id = command.getParameterMap().get(WarframeParameterNames.WARFRAME_PARAMETER_SEARCH_BUTTON_ID.getName());
        var searchResults = getSearchResults(command.getCommandMessage().getGuild().getIdLong());
        var buttonIds = WarframeEmbedMessagesHelper.searchPanelButtonsIds;

        if (id.equals(buttonIds.get(5))) {
            command.getCommandMessage().getMessage().delete().queue();
        } else {
            var someResult = searchResults.get(id);
            // TODO: Add processing of chosen item
        }
        return true;
    }

    @Deprecated
    private boolean handleSearchMissionRewardCommand(SearchMissionRewardCommand command) {
        String searchedReward = command.getParameterMap().get(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName());
        var searchResults = rewardsDatabase.searchAllRewards(searchedReward);

        var optionalHighestChanceMatch = searchResults.stream().max(Comparator.comparingInt(TrigramSearchResult::getMatchCount)); //HERE IMPROVE

        if (optionalHighestChanceMatch.isPresent()) {
            var highestChanceMatch = optionalHighestChanceMatch.get();
            if (highestChanceMatch.isDirectMatch()) {
                String requestedItem = highestChanceMatch.getComparedString();
                Set<RewardSearchResult> rewards = rewardsDatabase.getRewardsFromMissions(requestedItem);
                WarframeEmbedMessagesHelper.createAndSendMissionRewardsEmbed(rewards, requestedItem, command);
            } else {
                if ((highestChanceMatch.getMatchCount() / highestChanceMatch.getSearchTrigramCount()) * 100 > searchCutOffPercentage) {
                    String requestedItem = highestChanceMatch.getComparedString();
                    Set<RewardSearchResult> rewards = rewardsDatabase.getRewardsFromMissions(requestedItem);
                    WarframeEmbedMessagesHelper.createAndSendMissionRewardsEmbed(rewards, requestedItem, command);
                } else {
                    command
                            .getCommandMessage()
                            .getMessage()
                            .reply("I couldn't find a reward with enough confidence to provide results.\nReward might be unavailable or your input wasn't precise enough")
                            .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));

                }
            }
        }
        command.getCommandMessage().getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
        return true;
    }
}
