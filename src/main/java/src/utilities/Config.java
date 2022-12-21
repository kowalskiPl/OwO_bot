package src.utilities;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class Config {
    private int shards;
    private Set<String> prefixes;
    private int frameBufferDuration;
    private int eventThreadPoolSize;

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

    public static class ConfigBuilder {
        public static Config build(Properties properties) {
            Config config = new Config();
            config.shards = Integer.parseInt(properties.getProperty("app.shards", "3"));
            config.prefixes = Arrays.stream(properties.getProperty("app.commands.prefixes", "!").split(",")).collect(Collectors.toSet());
            config.eventThreadPoolSize = Integer.parseInt(properties.getProperty("app.event.threadPoolSize", "1"));
            config.frameBufferDuration = Integer.parseInt(properties.getProperty("app.music.framebufferDuration", "5000"));

            return config;
        }
    }
}
