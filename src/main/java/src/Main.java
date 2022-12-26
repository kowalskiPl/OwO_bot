package src;

import src.database.MongoConnectionPool;
import src.database.MongoDbContext;
import src.listeners.MainMessageListener;
import src.listeners.MusicListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.utilities.Config;
import src.utilities.ConfigReader;
import src.utilities.ServiceContext;

import javax.naming.ConfigurationException;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static String token;
    private static String conString;

    public static void main(String[] args) {
        log.info("Starting up");
        token = args[0];
        log.info("Loading config");
        loadConfig();
        conString = args[1];
        log.info("Setting up DB connection");
        var dbContext = new MongoDbContext(conString, 3, ServiceContext.getConfig().getMongoDb());
        ServiceContext.provideDbConnectionPool(dbContext);

        JDABuilder builder = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);
        builder.addEventListeners(new MainMessageListener(), new MusicListenerAdapter())
                .setActivity(Activity.playing("Honk"))
                .enableCache(CacheFlag.VOICE_STATE);

        // sharding setup
        for (int i = 0; i < 3; i++) {
            builder.useSharding(i, 3);
            try {
                builder.build();
            } catch (LoginException e) {
                log.error("Startup failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void loadConfig() {
        var loader = Main.class.getClassLoader();
        var inputStream = loader.getResourceAsStream("application.config");
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
