package src;

import src.listeners.MainMessageListener;
import src.listeners.MusicListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static String token;

    public static void main(String[] args) {
        log.info("Starting up");
        token = args[0];

        JDABuilder builder = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES);
        builder.addEventListeners(new MainMessageListener(), new MusicListenerAdapter())
                .setActivity(Activity.playing("Honk"))
                .enableCache(CacheFlag.VOICE_STATE);

        // sharding setup
        for (int i = 0; i < 10; i++) {
            builder.useSharding(i, 10);
            try {
                builder.build();
            } catch (LoginException e) {
                log.error("Startup failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
