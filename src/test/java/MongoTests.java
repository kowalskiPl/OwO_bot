import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.owobot.database.MongoDbContext;
import com.owobot.model.database.GuildSettings;
import com.owobot.utilities.Config;
import com.owobot.utilities.ConfigReader;
import com.owobot.utilities.ServiceContext;

import javax.naming.ConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MongoTests {
    private static final Logger log = LoggerFactory.getLogger(MongoTests.class);

    @BeforeAll
    static void prepareConfig() {
        loadConfig();
    }

    @Test
    public void connectionPoolTests(){
        System.out.println(ServiceContext.getConfig().getTestMongoDb());
        MongoDbContext context = new MongoDbContext("", 3, ServiceContext.getConfig().getTestMongoDb());
        Set<Long> aa = new HashSet<>();
        aa.add(123123L);
        GuildSettings settings = new GuildSettings(123123123L, aa, 100);
        context.saveNewGuildSettings(settings);
        var newSettings = context.getGuildSettingsByGuildID(123123123L);
        newSettings.setVolume(131);
        context.updateSettings(newSettings);
        System.out.println(context.getGuildSettingsByGuildID(123123123L));
        context.shutdown();
    }

    private static void loadConfig() {
        var loader = MongoTests.class.getClassLoader();
        var inputStream = loader.getResourceAsStream("application_test.config");
        try {
            ServiceContext.provideConfig(Config.ConfigBuilder.build(ConfigReader.readConfig(inputStream)));
        } catch (ConfigurationException e) {
            log.error("Config load failed!");
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
