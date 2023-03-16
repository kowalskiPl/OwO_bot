package com.owobot.modules.warframe;

import com.owobot.modules.warframe.model.MissionRewards;
import com.owobot.modules.warframe.model.Reward;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLDropTableParser {
    private String url;
    private Document document;
    private final Pattern rarityPattern = Pattern.compile("(.*?)\\((\\d{1,3}.?\\d{1,2})%\\)");
    private final Pattern missionTypePattern = Pattern.compile("(.*?)/(.*?) \\((.*?)\\)");

    private static final Logger log = LoggerFactory.getLogger(HTMLDropTableParser.class);

    public HTMLDropTableParser(String url) throws IOException {
        this.url = url;
        document = Jsoup.connect(this.url).get();
        log.info("Loaded file named: " + document.title());
    }

    public Set<MissionRewards> parseMissionRewards() {
        List<String> listOfPlaces = cleanupPlaces();

        List<MissionRewards> rewardsList = new ArrayList<>();

        listOfPlaces.forEach(place -> {
            MissionRewards missionRewards = new MissionRewards();
            String[] rows = place.split("\\n");
            String rewardsA = StringUtils.substringBetween(place, "Rotation A", "Rotation B");
            String rewardsB = StringUtils.substringBetween(place, "Rotation B", "Rotation C");
            String rewardsC = StringUtils.substringAfter(place, "Rotation C");
            Matcher mission = missionTypePattern.matcher(rows[0]);
            if (mission.find()) {
                missionRewards.setMissionName(mission.group(2));
                missionRewards.setPlanet(mission.group(1));
                missionRewards.setMissionType(mission.group(3));

                if (rewardsA != null) {
                    List<Reward> aRewards = parseRewards(rewardsA);
                    missionRewards.setRotationARewards(aRewards);
                }

                if (rewardsB != null) {
                    List<Reward> bRewards = parseRewards(rewardsB);
                    missionRewards.setRotationBRewards(bRewards);
                }

                if (!rewardsC.isEmpty()) {
                    List<Reward> cRewards = parseRewards(rewardsC);
                    missionRewards.setRotationCRewards(cRewards);
                }
                rewardsList.add(missionRewards);
            }
        });
        return new HashSet<>(rewardsList);
    }

    private List<Reward> parseRewards(String rotationRewards) {
        rotationRewards = rotationRewards.trim();
        List<Reward> aRewards = new ArrayList<>();
        String[] rewards = rotationRewards.split("\\n");
        for (int i = 0; i < rewards.length; i += 2) {
            Matcher m = rarityPattern.matcher(rewards[i+1]);
            m.find();
            String rarity = m.group(1).trim();
            String percentage = m.group(2).trim();
            aRewards.add(new Reward(rewards[i].trim(), rarity, Double.parseDouble(percentage)));
        }
        return aRewards;
    }

    private List<String> cleanupPlaces() {
        Element rewardTable = document.select("table").get(0);
        String table = rewardTable.toString()
                .replace("<table>", "")
                .replace("<tbody>", "")
                .replace("<tr>", "")
                .replace("</table>", "")
                .replace("</tbody>", "")
                .replace("</tr>", "")
                .replaceAll("(?m)^[ \\t]*\\r?\\n", "");

        String[] places = table.split("<tr class=\"blank-row\">\n" +
                " {3}<td class=\"blank-row\" colspan=\"2\"></td>");

        List<String> listOfPlaces = new ArrayList<>();
        for (var place : places) {
            String tmp = place.replace("<th colspan=\"2\">", "")
                    .replace("</th>", "")
                    .replace("<td>", "")
                    .replace("</td>", "").trim();
            listOfPlaces.add(tmp);
        }
        listOfPlaces.remove(listOfPlaces.size() - 1); // get rid of last empty one
        return listOfPlaces;
    }
}
