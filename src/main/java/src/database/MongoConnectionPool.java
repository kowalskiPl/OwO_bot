package src.database;

import src.utilities.ServiceContext;

import java.util.ArrayList;
import java.util.List;

public class MongoConnectionPool implements ConnectionPool {

    private final List<DbConnection> freeConnections;
    private final List<DbConnection> takenConnections;
    private final String connectionString;
    private final String mongoDatabaseName;

    public MongoConnectionPool(String connectionString, String mongoDatabaseName) {
        this.connectionString = connectionString;
        this.mongoDatabaseName = mongoDatabaseName;
        freeConnections = new ArrayList<>();
        takenConnections = new ArrayList<>();
    }

    @Override
    public void initialize(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            freeConnections.add(new DbConnection(connectionString, mongoDatabaseName));
        }
    }

    @Override
    public synchronized void shutdown() {
        freeConnections.forEach(DbConnection::shutDown);
        takenConnections.forEach(DbConnection::shutDown);
    }

    @Override
    public synchronized DbConnection getConnection() {
        DbConnection connection;
        if (!freeConnections.isEmpty()) {
            connection = freeConnections.get(freeConnections.size() - 1);
            freeConnections.remove(connection);
        } else {
            connection = new DbConnection(connectionString, mongoDatabaseName);
        }
        takenConnections.add(connection);
        return connection;
    }

    @Override
    public synchronized void returnConnection(DbConnection connection) {
        takenConnections.remove(connection);
        freeConnections.add(connection);
    }
}
