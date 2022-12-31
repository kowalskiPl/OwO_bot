package src.utilities;

import src.database.MongoDbContext;
import src.utilities.listener.EventListenerStack;

public class ServiceContext {
    private static Config config;
    private static MongoDbContext dbContext;
    private static EventListenerStack listenerStack;

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

    public static void provideEventListenerStack(EventListenerStack stack) {
        if (listenerStack != null) {
            throw new IllegalArgumentException("Event listener stack has been already defined!");
        }
        listenerStack = stack;
    }

    public static Config getConfig() {
        return config;
    }

    public static MongoDbContext getDbContext() {
        return dbContext;
    }

    public static EventListenerStack getListenerStack() {
        return listenerStack;
    }
}
