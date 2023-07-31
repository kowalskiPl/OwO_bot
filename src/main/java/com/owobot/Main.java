package com.owobot;

import com.owobot.utilities.Config;
import com.owobot.utilities.ConfigReader;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        Config config = null;
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
            config = loadConfig(false);
            if (config == null){
                log.error("Failed to load config");
                System.exit(1);
            }
            config.setConnectionString(conString);
            config.setDiscordToken(token);
        } else {
            log.info("Loading config");
            config = loadConfig(true);
            if (config == null){
                log.error("Failed to load config");
                System.exit(1);
            }
        }

        OwoBot owoBot = new OwoBot(config);

        log.info("Startup complete");

    }

    private static Config loadConfig(boolean secrets) {
        var loader = Main.class.getClassLoader();
        var inputStream = loader.getResourceAsStream("application.config");
        try {
            if (secrets) {
                FileInputStream fis = new FileInputStream(secretsFile);
                var config =  Config.ConfigBuilder.build(ConfigReader.readConfig(inputStream), ConfigReader.readConfig(fis));
                fis.close();
                return config;
            } else {
                return Config.ConfigBuilder.build(ConfigReader.readConfig(inputStream));
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
        return null;
    }
}
