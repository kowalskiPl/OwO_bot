package com.owobot.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DbConnection {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public DbConnection(String connectionString, String databaseName) {
        mongoClient = MongoClients.create(connectionString);
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        database = mongoClient.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
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
