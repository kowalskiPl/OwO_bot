package src.utilities;

import src.database.MongoDbContext;

public class ServiceContext {
    private static Config config;
    private static MongoDbContext dbContext;

    public static void provideConfig(Config newConfig) {
        if (config != null) {
            throw new IllegalArgumentException("Config has been already defined!");
        }
        config = newConfig;
    }

    public static void provideDbConnectionPool(MongoDbContext context) {
        if (dbContext != null) {
            throw new IllegalArgumentException("Connection pool has been already defined!");
        }
        dbContext = context;
    }

    public static Config getConfig() {
        return config;
    }
}
