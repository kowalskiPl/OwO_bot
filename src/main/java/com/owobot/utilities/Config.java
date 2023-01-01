package com.owobot.utilities;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {
    private int shards;
    private Set<String> prefixes;
    private int frameBufferDuration;
    private int eventThreadPoolSize;
    private String mongoDb;
    private String testMongoDb;
    private String connectionString;
    private String discordToken;

    public Config() {
    }

    public int getShards() {
        return shards;
    }

    public Set<String> getPrefixes() {
        return prefixes;
    }

    public int getFrameBufferDuration() {
        return frameBufferDuration;
    }

    public int getEventThreadPoolSize() {
        return eventThreadPoolSize;
    }

    public String getMongoDb() {
        return mongoDb;
    }

    public String getTestMongoDb() {
        return testMongoDb;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
    }

    public static class ConfigBuilder {
        public static Config build(Properties standardProperties) {
            Config config = new Config();
            config.shards = Integer.parseInt(standardProperties.getProperty("app.shards", "3"));
            config.prefixes = Arrays.stream(standardProperties.getProperty("app.commands.prefixes", "!").split(",")).collect(Collectors.toSet());
            config.eventThreadPoolSize = Integer.parseInt(standardProperties.getProperty("app.event.threadPoolSize", "1"));
            config.frameBufferDuration = Integer.parseInt(standardProperties.getProperty("app.music.framebufferDuration", "5000"));
            config.mongoDb = standardProperties.getProperty("app.mongo.database", "OwO_bot_db");
            config.testMongoDb = standardProperties.getProperty("app.mongo.database.test", "OwO_bot_db_test");
            return config;
        }

        public static Config build(Properties standardProperties, Properties secrets) {
            var config = build(standardProperties);
            config.discordToken = secrets.getProperty("app.discord.token");
            config.connectionString = secrets.getProperty("app.database.connection.string");
            return config;
        }
    }
}
