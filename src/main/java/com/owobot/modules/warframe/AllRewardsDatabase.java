package com.owobot.modules.warframe;

import com.owobot.model.TrigramSearchResult;
import com.owobot.modules.warframe.model.MissionRewards;
import com.owobot.modules.warframe.model.RelicReward;
import com.owobot.modules.warframe.model.RewardSearchResult;
import com.owobot.utilities.TrigramStringSearch;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import java.io.IOException;
import java.util.*;

@Getter
public class AllRewardsDatabase {
    private static final String dataURL = "https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html";
    private Set<RelicReward> allRelicsWithRewards;
    private Set<MissionRewards> allMissionRewards;
    private Set<String> allRewardNames;

    public AllRewardsDatabase() throws IOException {
        HTMLDropTableParser parser = new HTMLDropTableParser(dataURL);
        WarframeDropTableParser tableParser = new WarframeDropTableParser(dataURL);
        tableParser.loadHTML();
        allRelicsWithRewards = parser.parseRelicRewards();
        allMissionRewards = tableParser.processMissionDropTable(tableParser.gatherRewardsFromTables().get(0));
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

        allRelicsWithRewards.forEach(relic ->{
            allRewardNames.addAll(relic.getCommonRewards());
            allRewardNames.addAll(relic.getUncommonRewards());
            allRewardNames.add(relic.getRareReward());
        });
    }

    @Deprecated
    public List<TrigramSearchResult> searchAllRewards(String query) {
        TrigramStringSearch search = new TrigramStringSearch();
        List<TrigramSearchResult> searchResults = new LinkedList<>();
        allRewardNames.forEach(rewardName -> {
            searchResults.add(search.compareStrings(query, rewardName));
        });
        return searchResults;
    }

    public List<ExtractedResult> searchAllRewards(String query, int limit){
        return FuzzySearch.extractTop(query, allRewardNames, limit);
    }

    public Set<RelicReward> getRewardFromRelic(String rewardName) {
        Set<RelicReward> relicRewards = new LinkedHashSet<>();
        allRelicsWithRewards.forEach(reward -> {
            if (reward.containsReward(rewardName)){
                relicRewards.add(reward);
            }
        });
        return relicRewards;
    }

    public Set<RewardSearchResult> getRewardsFromMissions(String rewardName) {
        Set<RewardSearchResult> missionRewards = new LinkedHashSet<>();
        allMissionRewards.forEach(reward -> {
            Optional<RewardSearchResult> searchResult = reward.searchReward(rewardName);
            searchResult.ifPresent(missionRewards::add);
        });
        return missionRewards;
    }
}
