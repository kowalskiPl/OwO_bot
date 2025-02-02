package com.owobot.modules.warframe;

import com.owobot.modules.warframe.model.*;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class AllRewardsDatabase {
    private static final String dataURL = "https://www.warframe.com/droptables";
    private Set<RelicRewards> allRelicsWithRewards;
    private Set<MissionRewards> allMissionRewards;
    private Set<String> allRewardNames;

    public AllRewardsDatabase() throws IOException {
        allRewardNames = new LinkedHashSet<>();
        allMissionRewards = new LinkedHashSet<>();
        allRelicsWithRewards = new LinkedHashSet<>();
        reloadDropTables();
    }

    public synchronized void reloadDropTables() throws IOException {
        allRewardNames.clear();
        allMissionRewards.clear();
        allRelicsWithRewards.clear();

        WarframeDropTableParser tableParser = new WarframeDropTableParser(dataURL);
        tableParser.loadHTML();
        var allTheRewards = tableParser.gatherRewardsFromTables();
        allMissionRewards = tableParser.processMissionDropTable(allTheRewards.get(0));
        allRelicsWithRewards = tableParser.processRelicDropTable(allTheRewards.get(1));
        processAllRewardNames();
    }

    private void processAllRewardNames() {
        allRewardNames = new HashSet<>();
        allMissionRewards.forEach(reward -> {
            reward.getRotationARewards().forEach(aReward -> allRewardNames.add(aReward.getName()));
            reward.getRotationBRewards().forEach(bReward -> allRewardNames.add(bReward.getName()));
            reward.getRotationCRewards().forEach(cReward -> allRewardNames.add(cReward.getName()));
            reward.getJustRewards().forEach(justReward -> allRewardNames.add(justReward.getName()));
        });

        var allRelicNames = allRelicsWithRewards.stream().map(RelicRewards::getRelicName).collect(Collectors.toSet());
        var kek = allRelicNames.stream().filter(relic -> allRewardNames.contains(relic)).collect(Collectors.toSet());

        allRelicsWithRewards = allRelicsWithRewards.stream().filter(relic -> kek.contains(relic.getRelicName())).collect(Collectors.toSet());
        allRelicsWithRewards.forEach(relic ->{
            allRewardNames.addAll(relic.getCommonRewards().stream().map(Reward::getName).toList());
            allRewardNames.addAll(relic.getUncommonRewards().stream().map(Reward::getName).toList());
            allRewardNames.add(relic.getRareReward().getName());
        });
    }
    public List<ExtractedResult> searchAllRewards(String query, int limit){
        return FuzzySearch.extractTop(query, allRewardNames, limit);
    }

    private Set<RelicRewardSearchResult> getRewardFromRelic(String rewardName) {
        Set<RelicRewardSearchResult> relicRewards = new LinkedHashSet<>();
        allRelicsWithRewards.forEach(reward -> {
            var possibleReward = reward.searchReward(rewardName);
            possibleReward.ifPresent(relicRewards::add);
        });
        return relicRewards;
    }

    private Set<MissionRewardSearchResult> getRewardsFromMissions(String rewardName) {
        Set<MissionRewardSearchResult> missionRewards = new LinkedHashSet<>();
        allMissionRewards.forEach(reward -> {
            var searchResult = reward.searchReward(rewardName);
            searchResult.ifPresent(missionRewards::add);
        });
        return missionRewards;
    }

    public Set<RewardSearchResult> getReward(String rewardName) {
        Set<RewardSearchResult> totalResults = new HashSet<>();
        totalResults.addAll(getRewardsFromMissions(rewardName));
        totalResults.addAll(getRewardFromRelic(rewardName));
        return totalResults;
    }

    public void getRewardsFromAllLocations(String rewardName) {

    }
}
