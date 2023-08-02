package com.owobot.modules.warframe;

import com.owobot.modules.warframe.model.MissionRewards;
import com.owobot.modules.warframe.model.RelicRewards;
import com.owobot.modules.warframe.model.Reward;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarframeDropTableParser {
    private final String url;
    private Document document;

    public WarframeDropTableParser(String url) {
        this.url = url;
    }

    private final Pattern missionNameLocationTypePattern = Pattern.compile("^(\\w.*?)/(.*?) \\((.*)\\) ?(\\w*?)$");
    private final Pattern missionRewardRarityAndChancePattern = Pattern.compile("^(\\D*?) \\((.*?)%\\)$");
    private final Pattern relicNamePattern = Pattern.compile("^(\\w*?) (.*?) (Relic) \\((Intact)\\)$");

    private final String ROTATION_A = "Rotation A";
    private final String ROTATION_B = "Rotation B";
    private final String ROTATION_C = "Rotation C";

    public void loadHTML() throws IOException {
        document = Jsoup.connect(url).get();
    }

    public void processDropTables() {

    }

    /**
     * @return List of Elements containing most important pieces of drop table
     */
    public List<Elements> gatherRewardsFromTables() {
        var tables = document.select("table");
        List<Elements> results = new ArrayList<>();
        tables.forEach(table -> {
            var someValues = table.getAllElements().get(0);
            results.add(someValues.select("th,td"));
        });
        return results;
    }

    private List<List<Element>> extractSingleSourceRewards(Elements dropTable) {
        List<List<Element>> listOfSourcesWithRewards = new ArrayList<>();
        List<Element> tmpElementList = new ArrayList<>();

        for (Element singleLine : dropTable) {
            if (singleLine.hasClass("blank-row")) {
                listOfSourcesWithRewards.add(new ArrayList<>(tmpElementList));
                tmpElementList.clear();
            } else {
                tmpElementList.add(singleLine);
            }
        }
        return listOfSourcesWithRewards;
    }

    public Set<MissionRewards> processMissionDropTable(Elements missionDropTable) {
        var listOfMissionsWithRewards = extractSingleSourceRewards(missionDropTable);

        Set<MissionRewards> standardMissionRewards = new LinkedHashSet<>();
        listOfMissionsWithRewards.forEach(mission -> {
            try {
                standardMissionRewards.add(processSingleMission(mission));
            } catch (WarframeRewardProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return standardMissionRewards;
    }

    public MissionRewards processSingleMission(@NotNull List<Element> missionElements) throws WarframeRewardProcessingException {
        Matcher matcher = missionNameLocationTypePattern.matcher(missionElements.get(0).text());
        MissionRewards rewards = new MissionRewards();
        if (matcher.find()) {
            rewards.setPlanet(matcher.group(1));
            rewards.setMissionName(matcher.group(2));
            rewards.setMissionType(matcher.group(3));
            rewards.setExtraFeatures(matcher.group(4));
        } else {
            throw new WarframeRewardProcessingException("Failed to extract mission data from drop table: " + missionElements.get(0).text());
        }
        List<Reward> tmpRewardsList = new ArrayList<>();
        boolean first = true;
        boolean isRotationBased = false;

        String name = "";
        String rarity = "";
        double chance = 0.0;
        for (int i = 1; i < missionElements.size(); i++) {
            if (missionElements.get(i).text().equals(ROTATION_A)) {
                first = true;
                isRotationBased = true;
                continue;
            }

            if (missionElements.get(i).text().equals(ROTATION_B)) {
                rewards.setRotationARewards(new ArrayList<>(tmpRewardsList));
                tmpRewardsList.clear();
                first = true;
                isRotationBased = true;
                continue;
            }

            if (missionElements.get(i).text().equals(ROTATION_C)) {
                rewards.setRotationBRewards(new ArrayList<>(tmpRewardsList));
                tmpRewardsList.clear();
                first = true;
                isRotationBased = true;
                continue;
            }

            if (first) {
                name = missionElements.get(i).text();
                first = false;
            } else {
                Matcher rarityChanceMatcher = missionRewardRarityAndChancePattern.matcher(missionElements.get(i).text());
                if (rarityChanceMatcher.find()) {
                    rarity = rarityChanceMatcher.group(1);
                    chance = Double.parseDouble(rarityChanceMatcher.group(2));
                    if (name.contains("Radiant") || name.contains("Flawless")) { // filter out non-intact relics
                        continue;
                    }

                    if (name.contains("Relic")) {
                        name = name.replace("Relic", "");
                        name = name.trim();
                    }
                    Reward reward = new Reward(name, rarity, chance);
                    tmpRewardsList.add(reward);
                }
                first = true;
            }
        }
        if (isRotationBased) {
            rewards.setRotationCRewards(new ArrayList<>(tmpRewardsList));
        } else {
            rewards.setJustRewards(new ArrayList<>(tmpRewardsList));
        }
        tmpRewardsList.clear();
        return rewards;
    }

    public Set<RelicRewards> processRelicDropTable(Elements relicDropTable) {
        var relicRewardsPerRelic = extractSingleSourceRewards(relicDropTable);

        Set<RelicRewards> allRelicRewards = new LinkedHashSet<>();
        relicRewardsPerRelic.forEach(relic -> {
            var reward = processSingleRelic(relic);
            reward.ifPresent(allRelicRewards::add);
        });

        return allRelicRewards;
    }

    private Optional<RelicRewards> processSingleRelic(List<Element> relicElements) {
        Matcher relicMatcher = relicNamePattern.matcher(relicElements.get(0).text());
        if (relicMatcher.find()) {
            var name = relicMatcher.group(1) + " " + relicMatcher.group(2);
            List<Reward> commonRewards = new ArrayList<>();
            List<Reward> uncommonRewards = new ArrayList<>();
            Reward rareReward = new Reward();

            for (int i = 1; i < relicElements.size(); i += 2) {
                var currentElement = relicElements.get(i);
                var nextElement = relicElements.get(i + 1);

                if (nextElement.text().equals("Uncommon (25.33%)")) {
                    commonRewards.add(new Reward(currentElement.text(), "Common", 25.33));
                }

                if (nextElement.text().equals("Uncommon (11.00%)")) {
                    uncommonRewards.add(new Reward(currentElement.text(), "Uncommon", 11.00));
                }

                if (nextElement.text().equals("Rare (2.00%)")) {
                    rareReward = new Reward(currentElement.text(), "Rare", 2.00);
                }
            }
            return Optional.of(new RelicRewards(name, commonRewards, uncommonRewards, rareReward));
        } else {
            return Optional.empty();
        }
    }

    private void processKeyDropTable(Elements keyDropTable) {

    }

    private void processTransientDropTable(Elements transientDropTable) {

    }

    private void processSortieDropTable(Elements sortieDropTable) {

    }

    private void processCetusDropTable(Elements cetusDropTable) {

    }

    private void processSolarisDropTable(Elements solarisDropTable) {

    }

    private void processDeimosDropTable(Elements deimosDropTable) {

    }

    private void processZarimanDropTable(Elements zarimanDropTable) {

    }

}
