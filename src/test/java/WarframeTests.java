import com.owobot.model.TrigramSearchResult;
import com.owobot.modules.warframe.AllRewardsDatabase;
import com.owobot.modules.warframe.HTMLDropTableParser;
import com.owobot.modules.warframe.WarframeDropTableParser;
import com.owobot.utilities.TrigramStringSearch;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Comparator;

public class WarframeTests {

    private static final String dataURL = "https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html";

    @Test
    void queryTest() {
        try {
            HTMLDropTableParser parser = new HTMLDropTableParser(dataURL);
            System.out.println(parser.parseRelicRewards());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void trigramTest() {
        String query = "Cookie with cream";
        String stringToSearch = "Cookie with cream";
        TrigramStringSearch search = new TrigramStringSearch();
        var result = search.compareStrings(query, stringToSearch);
        System.out.println(result);
        assert result.getMatchCount() == Integer.MAX_VALUE;
    }

    @Test
    void relicRewardSearchTest() {
        try {
            AllRewardsDatabase database = new AllRewardsDatabase();
            var results = database.searchAllRewards("Braon Pime Stok")
                    .stream()
                    .max(Comparator.comparingInt(TrigramSearchResult::getMatchCount));
            System.out.println(results);
            assert results.get().getComparedString().equals("Braton Prime Stock");

            var relics = database.getRewardFromRelic(results.get().getComparedString());
            assert relics != null;
            relics.forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMissionRewards() {
        try {
            AllRewardsDatabase database = new AllRewardsDatabase();
            var results = database.searchAllRewards("Trinity Chassis")
                    .stream()
                    .filter(result -> result.getCheckedTrigramCount() - result.getMatchCount() < 8)
                    .max(Comparator.comparingInt(TrigramSearchResult::getMatchCount));
            System.out.println(results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void fuzzyTest(){
        try {
            AllRewardsDatabase database = new AllRewardsDatabase();
            var results = FuzzySearch.extractTop("braton prie stk", database.getAllRewardNames(), 5);
            System.out.println(results);
            assert "Braton Prime Stock".equals(results.get(0).getString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parserMissionRewardsTest() {
        WarframeDropTableParser parser = new WarframeDropTableParser("https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html");
        try {
            parser.loadHTML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var someRewards = parser.processMissionDropTable(parser.gatherRewardsFromTables().get(0));
        var kek = someRewards.stream().toList();
        System.out.println(kek.get(kek.size() - 1));
        assert kek.get(kek.size() - 1).getPlanet().equals("Duviri");
        assert kek.get(kek.size() - 1).getJustRewards().get(0).getName().equals("25X Steel Essence");
    }
}
