package trevron.utility;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Config config = new Config();

    // Database sign in info obscured by config file

    private static final String dbURL = config.getDbURL();
    private static final String dbUserName = config.getDbUserName();
    private static final String dbPassword = config.getDbPassword();
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    static Connection conn;

    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(dbURL, dbUserName, dbPassword);
        System.out.println("Connection successful!");

    }

    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception {
        conn.close();
        System.out.println("Connection closed!");
    }

}
