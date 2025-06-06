package org.example.database;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class DBManager {
    private Connection connection;

    public void connect() throws SQLException {
        Dotenv dotenv = Dotenv.load();

        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        connection = DriverManager.getConnection(url, user, password);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET foreign_key_checks = 1");
        }

        connection.setAutoCommit(false);
    }

    /**
     * Commit all changes since the connection was opened OR since the last commit/rollback
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Rollback to the last commit, or when the connection was opened
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Ends the connection to the database
     */
    public void disconnect() throws SQLException {
        connection.close();
    }

    public Connection getConnection() {
        return connection;
    }

}
