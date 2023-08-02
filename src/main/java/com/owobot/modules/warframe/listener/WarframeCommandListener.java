package com.owobot.modules.warframe.listener;

import com.owobot.OwoBot;
import com.owobot.async.NamedThreadFactory;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.warframe.AllRewardsDatabase;
import com.owobot.modules.warframe.WarframeEmbedMessagesManager;
import com.owobot.modules.warframe.WarframeParameterNames;
import com.owobot.modules.warframe.commands.SearchMissionRewardCommand;
import com.owobot.modules.warframe.commands.WarframeSearchButtonPressCommand;
import com.owobot.utilities.Reflectional;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WarframeCommandListener extends Reflectional implements CommandListener {
    private final AllRewardsDatabase rewardsDatabase;
    private final int searchCutOffPercentage = 89;
    private final Logger log = LoggerFactory.getLogger(WarframeCommandListener.class);
    private final Map<Long, Map<String, String>> userSearchResults;
    private final WarframeEmbedMessagesManager embedMessagesManager;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Warframe-Drop-Table-Updater"));


    public WarframeCommandListener(OwoBot owoBot) {
        super(owoBot);
        embedMessagesManager = new WarframeEmbedMessagesManager();
        userSearchResults = new LinkedHashMap<>();
        try {
            rewardsDatabase = new AllRewardsDatabase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executorService.scheduleAtFixedRate(() -> {
            try {
                log.info("Refreshing Warframe drop table");
                rewardsDatabase.reloadDropTables();
            } catch (IOException e) {
                log.error("There was an error during Warframe drop table refresh: {}", e.getMessage());
            }
        }, 1, 1, TimeUnit.HOURS);
    }

    private synchronized void updateSearchResults(Long userId, Map<String, String> newResults) {
        if (!userSearchResults.containsKey(userId)) {
            userSearchResults.put(userId, newResults);
        } else {
            userSearchResults.get(userId).clear();
            userSearchResults.get(userId).putAll(newResults);
        }
    }

    private synchronized Map<String, String> getSearchResults(Long userId) {
        return userSearchResults.get(userId);
    }

    private synchronized void removeSearchResults(Long userId) {
        userSearchResults.remove(userId);
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

    @Override
    public void shutdown() {
        embedMessagesManager.killLongLivingEmbeds();
        executorService.shutdown();
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

        if (getSearchResults(command.getCommandMessage().getUser().getIdLong()) != null) {
            command.getCommandMessage()
                    .getMessage()
                    .getChannel()
                    .sendMessage("You already have an outstanding query active").queue(message -> message.delete().queueAfter(20, TimeUnit.SECONDS));
            return true;
        }

        var searchedReward = command.getParameterMap().get(WarframeParameterNames.WARFRAME_PARAMETER_REWARD_SEARCH_STRING.getName());
        var searchResults = rewardsDatabase.searchAllRewards(searchedReward, 5);
        var listOfResults = searchResults.stream().map(ExtractedResult::getString).toList();

        Map<String, String> resultsMappedToIds = IntStream.range(0, listOfResults.size())
                .boxed()
                .collect(Collectors.toMap(WarframeEmbedMessagesManager.searchPanelButtonsIds::get, listOfResults::get));

        updateSearchResults(command.getCommandMessage().getUser().getIdLong(), resultsMappedToIds);

        var directMatch = searchResults.stream().max(Comparator.comparing(ExtractedResult::getScore)).stream().findFirst();

        if (directMatch.isPresent()) {
            if (directMatch.get().getScore() == 100) {
                var rewardSearchResults = new ArrayList<>(rewardsDatabase.getReward(directMatch.get().getString()).stream().toList());
                Collections.sort(rewardSearchResults);
                Collections.reverse(rewardSearchResults);
                embedMessagesManager.createAndSendRewardsEmbed(rewardSearchResults, directMatch.get().getString(), command);
                return true;
            }
        }

        if (!searchResults.isEmpty() && directMatch.isPresent()) {
            if (directMatch.get().getScore() < searchCutOffPercentage) {
                var msg = command.getCommandMessage()
                        .getMessage()
                        .getChannel()
                        .sendMessage("The item you are looking for cannot be found in the current drop tables, please verify your query and check if the item is unvaulted");
                if (command.getCommandMessage().isGuildMessage()){
                    msg.queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                } else {
                    msg.queue();
                }
                removeSearchResults(command.getCommandMessage().getUser().getIdLong());
                return true;
            }
            embedMessagesManager.createAndSendRewardSearchEmbed(listOfResults, searchedReward, command, userSearchResults);
        }
        if (command.getCommandMessage().isGuildMessage())
            command.getCommandMessage().getMessage().delete().queueAfter(40, TimeUnit.SECONDS);
        return true;
    }

    private boolean handleSearchButtonPressCommand(WarframeSearchButtonPressCommand command) {
        var id = command.getParameterMap().get(WarframeParameterNames.WARFRAME_PARAMETER_SEARCH_BUTTON_ID.getName());
        var searchResults = getSearchResults(command.getCommandMessage().getUser().getIdLong());
        var buttonIds = WarframeEmbedMessagesManager.searchPanelButtonsIds;

        if (id.equals(buttonIds.get(5))) {
            embedMessagesManager.removeUserEmbed(command.getCommandMessage().getUser().getIdLong());
            removeSearchResults(command.getCommandMessage().getUser().getIdLong());
        } else {
            var someResult = searchResults.get(id);
            var rewards = new ArrayList<>(rewardsDatabase.getReward(someResult));
            Collections.sort(rewards);
            Collections.reverse(rewards);
            embedMessagesManager.removeUserEmbed(command.getCommandMessage().getUser().getIdLong());
            embedMessagesManager.createAndSendRewardsEmbed(rewards, someResult, command);
            removeSearchResults(command.getCommandMessage().getUser().getIdLong());
        }
        return true;
    }
}
