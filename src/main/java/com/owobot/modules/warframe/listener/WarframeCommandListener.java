package com.owobot.modules.warframe.listener;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.model.TrigramSearchResult;
import com.owobot.modules.warframe.AllRewardsDatabase;
import com.owobot.modules.warframe.WarframeEmbedMessagesHelper;
import com.owobot.modules.warframe.WarframeParameterNames;
import com.owobot.modules.warframe.commands.SearchMissionRewardCommand;
import com.owobot.modules.warframe.model.RewardSearchResult;
import com.owobot.utilities.Reflectional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WarframeCommandListener extends Reflectional implements CommandListener {
    private final AllRewardsDatabase rewardsDatabase;
    private final int searchCutOffPercentage = 50;
    private static final Logger log = LoggerFactory.getLogger(WarframeCommandListener.class);

    public WarframeCommandListener(OwoBot owoBot) {
        super(owoBot);
        try {
            rewardsDatabase = new AllRewardsDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(Command command) {
        if (command instanceof SearchMissionRewardCommand relicRewardCommand) {
            return handleMissionRewardSearchCommand(relicRewardCommand);
        }

        return false;
    }

    private boolean handleMissionRewardSearchCommand(SearchMissionRewardCommand command) {
        if (command.getParameterMap().containsKey(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName())) {
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
        } else {
            command.getCommandMessage().getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
            return false;
        }
    }
}
