package src.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DbConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private String databaseName;

    public DbConnection(String connectionString, String databaseName) {
        mongoClient = MongoClients.create(connectionString);
        this.databaseName = databaseName;
        database = mongoClient.getDatabase(databaseName);
    }

    public void shutDown() {
        mongoClient.close();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
