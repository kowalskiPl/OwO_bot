package src.utilities;

public class ServiceContext {
    private static Config config;

    public static void provideConfig(Config newConfig) {
        if (config != null) {
            throw new IllegalArgumentException("Config has been already defined!");
        }
        config = newConfig;
    }

    public static Config getConfig() {
        return config;
    }
}
