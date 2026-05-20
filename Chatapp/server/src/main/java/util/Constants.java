package util;

public class Constants {

    public static final int PORT = 5000;

    public static final String DB_URL =
            "jdbc:mysql://localhost:3306/chatapp?useSSL=false";

    public static final String DB_USER = "root";

    public static final String DB_PASS = "";
    // Keep existing chat history available for reload on client connect
    public static final boolean DB_RESET_ON_START = false;
}