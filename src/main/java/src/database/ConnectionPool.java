package src.database;

public interface ConnectionPool {
    void initialize(int poolSize);
    void shutdown();
    DbConnection getConnection();
    void returnConnection(DbConnection connection);
}
