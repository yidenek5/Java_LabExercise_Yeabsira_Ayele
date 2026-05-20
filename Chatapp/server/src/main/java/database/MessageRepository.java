package database;

import model.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    private static final int FILE_CHUNK_SIZE = 256 * 1024;

    private Connection conn;

    public MessageRepository() {
        conn = DatabaseManager.getConnection();
        try {
            if (conn != null) {
                DatabaseMetaData md = conn.getMetaData();
                ResultSet tables = md.getTables(null, null, "messages", null);
                if (!tables.next()) {
                    System.out.println("[MessageRepository] table 'messages' not found - disabling DB saves");
                    conn = null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null;
        }
    }

    public void save(Message msg) {

        try {
            if (conn == null) {
                System.out.println("[MessageRepository] DB connection not available - skipping save");
                return;
            }

            byte[] fileData = msg.getFileData();
            long messageId = insertMessage(msg, fileData, true);

            if (fileData != null && fileData.length > FILE_CHUNK_SIZE) {
                saveFileChunks(messageId, fileData);
            }

            verifyStoredFileState(messageId);

        } catch (Exception e) {
            e.printStackTrace();
            // Disable DB to avoid repeated SQL errors until schema is fixed
            try {
                conn.rollback();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                // ignore
            }
            conn = null;
        }
    }

    public List<Message> getAll() {

        List<Message> list = new ArrayList<>();

        try {
            if (conn == null) {
            System.out.println("[MessageRepository] DB not available - getAll returns empty list");
            return list;
            }

            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM messages ORDER BY id ASC"
            );

            while (rs.next()) {
                long id = rs.getLong("id");
                byte[] fileData = rs.getBytes("file_data");

                if ((fileData == null || fileData.length == 0) && rs.getString("file_name") != null) {
                    fileData = loadFileChunks(id);
                }

                list.add(
                    new Message(
                        rs.getString("sender"),
                        rs.getString("type"),
                        rs.getString("message"),
                        rs.getString("file_name"),
                        fileData
                    )
                );
            }
} catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private void saveFileChunks(long messageId, byte[] data) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO message_file_chunks(message_id, chunk_index, chunk_data) VALUES(?,?,?)")) {

            int chunkIndex = 0;
            for (int offset = 0; offset < data.length; offset += FILE_CHUNK_SIZE) {
                int length = Math.min(FILE_CHUNK_SIZE, data.length - offset);
                byte[] chunk = new byte[length];
                System.arraycopy(data, offset, chunk, 0, length);

                ps.setLong(1, messageId);
                ps.setInt(2, chunkIndex++);
                ps.setBytes(3, chunk);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private byte[] loadFileChunks(long messageId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT chunk_data FROM message_file_chunks WHERE message_id = ? ORDER BY chunk_index ASC")) {
            ps.setLong(1, messageId);

            try (ResultSet rs = ps.executeQuery(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                while (rs.next()) {
                    out.write(rs.getBytes("chunk_data"));
                }
                return out.toByteArray();
            } catch (Exception e) {
                throw new SQLException("Failed to reconstruct file chunks for message " + messageId, e);
            }
        }
    }

    private void verifyStoredFileState(long messageId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT OCTET_LENGTH(file_data) AS file_data_bytes FROM messages WHERE id = ?")) {
            ps.setLong(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[MessageRepository] verify message id=" + messageId + " file_data_bytes=" + rs.getLong("file_data_bytes"));
                }
            }
        } catch (Exception e) {
            System.out.println("[MessageRepository] verifyStoredFileState failed for message id=" + messageId + ": " + e.getMessage());
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) AS chunk_count FROM message_file_chunks WHERE message_id = ?")) {
            ps.setLong(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[MessageRepository] verify message id=" + messageId + " chunk_count=" + rs.getLong("chunk_count"));
                }
            }
        } catch (Exception e) {
            System.out.println("[MessageRepository] verifyStoredFileState chunks failed for message id=" + messageId + ": " + e.getMessage());
        }
    }

    private long insertMessage(Message msg, byte[] fileData, boolean allowFallback) throws SQLException {
        conn.setAutoCommit(false);

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO messages(sender,message,type,file_name,file_data) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, msg.getSender());
            ps.setString(2, msg.getMessage());
            ps.setString(3, msg.getType());
            ps.setString(4, msg.getFileName());

            if (fileData != null && fileData.length > 0) {
                ps.setBinaryStream(5, new ByteArrayInputStream(fileData), fileData.length);
            } else {
                ps.setNull(5, Types.BLOB);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Failed to read generated message id");
                }

                long messageId = keys.getLong(1);
                conn.commit();
                conn.setAutoCommit(true);

                if (fileData == null || fileData.length == 0) {
                    System.out.println("[MessageRepository] saved message id=" + messageId + " no file data stored in messages.file_data");
                } else {
                    System.out.println("[MessageRepository] saved message id=" + messageId + " stored file_data bytes=" + fileData.length + " in messages.file_data");
                }

                return messageId;
            }
        } catch (SQLException firstError) {
            if (!allowFallback || fileData == null || fileData.length == 0) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw firstError;
            }

            System.out.println("[MessageRepository] direct BLOB insert failed, retrying with chunk storage: " + firstError.getMessage());
            conn.rollback();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO messages(sender,message,type,file_name,file_data) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, msg.getSender());
                ps.setString(2, msg.getMessage());
                ps.setString(3, msg.getType());
                ps.setString(4, msg.getFileName());
                ps.setNull(5, Types.BLOB);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to read generated message id");
                    }
                    long messageId = keys.getLong(1);
                    conn.commit();
                    conn.setAutoCommit(true);
                    System.out.println("[MessageRepository] saved message id=" + messageId + " using chunk fallback, total bytes=" + fileData.length);
                    return messageId;
                }
            }
        }
    }
}