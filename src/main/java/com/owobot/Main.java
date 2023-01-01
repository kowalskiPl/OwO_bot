package com.owobot;

import com.owobot.database.MongoDbContext;
import com.owobot.utilities.Config;
import com.owobot.utilities.ConfigReader;
import com.owobot.utilities.ServiceContext;
import com.owobot.utilities.listener.EventListenerStack;
import com.owobot.utilities.listener.MusicEmbedMessageSender;
import org.apache.commons.cli.*;
import com.owobot.messagelisteners.MainMessageListener;
import com.owobot.messagelisteners.MusicListenerAdapter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.owobot.utilities.listener.BasicMessageHandler;

import javax.naming.ConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static String secretsFile;
    public static void main(String[] args) {
        Options options = new Options();
        Option secrets = new Option("s", "secrets", true, "File containing secrets");
        secrets.setRequired(false);
        options.addOption(secrets);

        Option discordToken = new Option("t", "token", true, "Discord bot token");
        discordToken.setRequired(false);
        options.addOption(discordToken);

        Option databaseConString = new Option("c", "connection-string", true, "MongoDb connection string");
        databaseConString.setRequired(false);
        options.addOption(databaseConString);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("OwO-bot", options);
            System.exit(1);
            e.printStackTrace();
        }

        secretsFile = cmd.getOptionValue("secrets");
        String token = cmd.getOptionValue("token");
        String conString = cmd.getOptionValue("connection-string");

        log.info("Starting up");


        if (secretsFile == null) {
            if (token == null) {
                log.error("No discord token provided!");
                System.exit(1);
            }
            if (conString == null) {
                log.error("No database connection string provided!");
                System.exit(1);
            }
            log.info("Loading config");
            loadConfig(false);
            ServiceContext.getConfig().setConnectionString(conString);
            ServiceContext.getConfig().setDiscordToken(token);
        } else {
            log.info("Loading config");
            loadConfig(true);
        }

        log.info("Setting up DB connection");
        var dbContext = new MongoDbContext(ServiceContext.getConfig().getConnectionString(), 3, ServiceContext.getConfig().getMongoDb());
        ServiceContext.provideDbConnectionPool(dbContext);

        log.info("Creating event listener stack");
        var listenerStack = new EventListenerStack(ServiceContext.getConfig().getEventThreadPoolSize());
        listenerStack.registerListener(new MusicEmbedMessageSender(), new BasicMessageHandler());
        ServiceContext.provideEventListenerStack(listenerStack);

        JDABuilder builder = JDABuilder.createDefault(ServiceContext.getConfig().getDiscordToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(new MainMessageListener(), new MusicListenerAdapter())
                .setActivity(Activity.playing("Honk"))
                .enableCache(CacheFlag.VOICE_STATE);

        OwoBot owoBot = new OwoBot();

        // sharding setup
        for (int i = 0; i < 3; i++) {
            builder.useSharding(i, 3);
            builder.build();
        }
        log.info("Startup complete");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Preparing to shutdown");
            ServiceContext.getDbContext().shutdown();
            ServiceContext.getListenerStack().shutdown();
        }));
    }

    private static void loadConfig(boolean secrets) {
        var loader = Main.class.getClassLoader();
        var inputStream = loader.getResourceAsStream("application.config");
        try {
            if (secrets) {
                FileInputStream fis = new FileInputStream(secretsFile);
                ServiceContext.provideConfig(Config.ConfigBuilder.build(ConfigReader.readConfig(inputStream), ConfigReader.readConfig(fis)));
                fis.close();
            } else {
                ServiceContext.provideConfig(Config.ConfigBuilder.build(ConfigReader.readConfig(inputStream)));
            }
        } catch (ConfigurationException | IOException e) {
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
