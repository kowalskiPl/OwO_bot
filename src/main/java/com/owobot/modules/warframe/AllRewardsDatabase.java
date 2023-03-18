package com.owobot.modules.warframe;

import com.owobot.model.TrigramSearchResult;
import com.owobot.modules.warframe.model.MissionRewards;
import com.owobot.modules.warframe.model.RelicReward;
import com.owobot.utilities.TrigramStringSearch;
import lombok.Getter;

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
        allRelicsWithRewards = parser.parseRelicRewards();
        allMissionRewards = parser.parseMissionRewards();
        processAllRewardNames();
    }

    private void processAllRewardNames() {
        allRewardNames = new HashSet<>();
        allMissionRewards.forEach(reward -> {
            reward.getRotationARewards().forEach(aReward -> allRewardNames.add(aReward.getName()));
            reward.getRotationBRewards().forEach(bReward -> allRewardNames.add(bReward.getName()));
            reward.getRotationCRewards().forEach(cReward -> allRewardNames.add(cReward.getName()));
        });

        allRelicsWithRewards.forEach(relic ->{
            allRewardNames.addAll(relic.getCommonRewards());
            allRewardNames.addAll(relic.getUncommonRewards());
            allRewardNames.add(relic.getRareReward());
        });
    }

    public List<TrigramSearchResult> searchAllRewards(String query) {
        TrigramStringSearch search = new TrigramStringSearch();
        List<TrigramSearchResult> searchResults = new LinkedList<>();
        allRewardNames.forEach(rewardName -> {
            searchResults.add(search.compareStrings(query, rewardName));
        });
        return searchResults;
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
}
