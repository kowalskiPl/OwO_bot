package com.owobot.modules.warframe.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
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
    private static final Map<Long, Map<String, String>> userSearchResults = new LinkedHashMap<>();


    public WarframeCommandListener(OwoBot owoBot) {
        super(owoBot);
        try {
            rewardsDatabase = new AllRewardsDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void updateSearchResults(Long userId, Map<String, String> newResults) {
        if (!userSearchResults.containsKey(userId)) {
            userSearchResults.put(userId, newResults);
        } else {
            userSearchResults.get(userId).clear();
            userSearchResults.get(userId).putAll(newResults);
        }
    }

    private synchronized Map<String, String> getSearchResults(Long guildId) {
        return userSearchResults.getOrDefault(guildId, Collections.emptyMap());
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

        updateSearchResults(command.getCommandMessage().getAuthor().getIdLong(), resultsMappedToIds);

        var directMatch = searchResults.stream().max(Comparator.comparing(ExtractedResult::getScore)).stream().findFirst();
        boolean directlyMatched = false;
        if (directMatch.isPresent()) {
            if (directMatch.get().getScore() == 100) {
                directlyMatched = true;
                // TODO: Add processing of direct match
                var rewardSearchResults = rewardsDatabase.getReward(directMatch.get().getString());
                command.getCommandMessage().getMessageChannel().sendMessage("Wow it's a direct match! Here grab it: ").queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
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
        var searchResults = getSearchResults(command.getCommandMessage().getAuthor().getIdLong());
        var buttonIds = WarframeEmbedMessagesHelper.searchPanelButtonsIds;

        if (id.equals(buttonIds.get(5))) {
            command.getCommandMessage().getMessage().delete().queue();
        } else {
            var someResult = searchResults.get(id);
            // TODO: Add processing of chosen item
        }
        return true;
    }

    private String prepareRewardTextThing(Set<RewardSearchResult> rewardSearchResults, int cutoff) {
        StringBuilder theNiceRewardText = new StringBuilder();

        int counter = 1;
        for (RewardSearchResult rewardSearchResult : rewardSearchResults) {
            if (rewardSearchResult.isRelic()) {

            }

            if (rewardSearchResult.getJustReward() != null) {

            }
        }

        return theNiceRewardText.toString();
    }
}
