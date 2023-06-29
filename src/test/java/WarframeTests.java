import com.owobot.modules.warframe.AllRewardsDatabase;
import com.owobot.modules.warframe.HTMLDropTableParser;
import com.owobot.modules.warframe.WarframeDropTableParser;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

    @Test
    void testRelicParser(){
        WarframeDropTableParser parser = new WarframeDropTableParser("https://n8k6e2y6.ssl.hwcdn.net/repos/hnfvc0o3jnfvc873njb03enrf56.html");
        try {
            parser.loadHTML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var someRewards = parser.processRelicDropTable(parser.gatherRewardsFromTables().get(1));
        var kek = someRewards.stream().toList();
        System.out.println(kek.get(kek.size() - 1));
    }
}
