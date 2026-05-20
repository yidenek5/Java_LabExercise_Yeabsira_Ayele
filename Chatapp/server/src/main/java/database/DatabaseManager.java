package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import util.Constants;

public class DatabaseManager {

    private static Connection connection;
    public static Connection getConnection() {

        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection( Constants.DB_URL, Constants.DB_USER, Constants.DB_PASS );
                createTables();  //create tables if they don't exist and ensure schema is up to date
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void createTables() {

        try {

            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS messages(" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "sender VARCHAR(100)," +
                            "message TEXT," +
                            "type VARCHAR(20)," +
                            "file_name VARCHAR(255)," +
                            "file_data LONGBLOB" +")"
            );

            ensureColumn(stmt, "messages", "sender", "VARCHAR(100)");
            ensureColumn(stmt, "messages", "message", "TEXT");
            ensureColumn(stmt, "messages", "type", "VARCHAR(20)");
            ensureColumn(stmt, "messages", "file_name", "VARCHAR(255)");
            ensureColumn(stmt, "messages", "file_data", "LONGBLOB");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS message_file_chunks(" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "message_id INT NOT NULL," +
                            "chunk_index INT NOT NULL," +
                            "chunk_data LONGBLOB NOT NULL," +
                            "INDEX idx_message_chunks(message_id, chunk_index)," +
                            "CONSTRAINT fk_message_chunks_message FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE)"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ensureColumn(Statement stmt, String tableName, String columnName, String definition) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, null, tableName, columnName)) {
                if (!columns.next()) {
                    stmt.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition);
                    System.out.println("[DatabaseManager] Added missing column '" + columnName + "' to table '" + tableName + "'");
                }
            }
        } catch (Exception e) {
            System.out.println("[DatabaseManager] Failed to ensure column '" + columnName + "': " + e.getMessage());
        }
    }
}