package src.database;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import src.model.database.GuildSettings;

import java.util.ArrayList;
import java.util.List;

public class MongoDbContext {
    private static final String SETTINGS_COLLECTION_NAME = "guildSettings";
    private static MongoConnectionPool connectionPool;
    private static final Logger log = LoggerFactory.getLogger(MongoDbContext.class);

    private static final List<GuildSettings> guildSettings = new ArrayList<>();

    public MongoDbContext(String conString, int poolSize, String databaseName) {
        connectionPool = new MongoConnectionPool(conString, databaseName);
        connectionPool.initialize(poolSize);
        initializeCache();
    }

    public void initializeCache() {
        var connection = connectionPool.getConnection();
        var guildCollection = connection.getDatabase().getCollection(SETTINGS_COLLECTION_NAME, GuildSettings.class);
        guildCollection.createIndex(Indexes.descending("guildId"), new IndexOptions().unique(true));
        guildCollection.find().forEach(guildSettings::add);
        connectionPool.returnConnection(connection);
    }

    public void shutdown() {
        connectionPool.shutdown();
    }

    public synchronized void saveNewGuildSettings(GuildSettings settings) {
        var connection = connectionPool.getConnection();
        var guildCollection = connection.getDatabase().getCollection(SETTINGS_COLLECTION_NAME, GuildSettings.class);
        try {
            guildCollection.insertOne(settings);
        } catch (MongoException e) {
            log.warn("Entry with given guild ID already exists!");
        }
        guildSettings.add(settings);
        connectionPool.returnConnection(connection);
    }

    public synchronized void updateSettings(GuildSettings settings) {
        var connection = connectionPool.getConnection();
        var guildCollection = connection.getDatabase().getCollection(SETTINGS_COLLECTION_NAME, GuildSettings.class);
        System.out.println(guildCollection.replaceOne(Filters.eq("guildId", settings.getGuildId()), settings));
        guildSettings.stream().filter(thisSettings -> thisSettings.getGuildId() == settings.getGuildId()).limit(1L).forEach(thisSettings -> thisSettings.update(settings));
    }

    public GuildSettings getGuildSettingsByGuildID(long guildId) {
        var settings = guildSettings.stream().filter(guildSettings1 -> guildSettings1.getGuildId() == guildId).findFirst();
        return settings.orElse(null);
    }

    public synchronized void deleteSettingsByGuildId(long guildId) {
        var connection = connectionPool.getConnection();
        var guildCollection = connection.getDatabase().getCollection(SETTINGS_COLLECTION_NAME, GuildSettings.class);
        guildCollection.deleteOne(Filters.eq("guildId", guildId));
        guildSettings.removeIf(guildSettings1 -> guildSettings1.getGuildId() == guildId);
        connectionPool.returnConnection(connection);
    }
}
