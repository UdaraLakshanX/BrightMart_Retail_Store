package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/brightmart_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "udara23@#";

    private static Connection connection = null;

    private DBConnection() {
    }

    private static String getPassword() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null) return env;
        String prop = System.getProperty("db.password");
        if (prop != null) return prop;
        return DEFAULT_PASSWORD;
    }

    public static synchronized Connection getConnection() {
        try {
            boolean needNewConn = (connection == null);
            if (!needNewConn) {
                try {
                    needNewConn = connection.isClosed() || !connection.isValid(2);
                } catch (SQLException ex) {
                    needNewConn = true;
                }
            }
            if (needNewConn) {
                Class.forName(DRIVER);
                String pwd = getPassword();
                try {
                    connection = DriverManager.getConnection(URL, USERNAME, pwd);
                } catch (SQLException sqle) {
                    if (DEFAULT_PASSWORD.equals(pwd)) {
                        try {
                            connection = DriverManager.getConnection(URL, USERNAME, "");
                        } catch (SQLException ignored) {
                            throw sqle;
                        }
                    } else {
                        throw sqle;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            connection = null;
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(
                    null,
                    "MySQL JDBC Driver not found! Please check your classpath.\nError: " + e.getMessage(),
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                System.err.println("Driver Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            connection = null;
            if (!java.awt.GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to connect to MySQL database ('brightmart_db').\nEnsure MySQL is running.\nError: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
                );
            } else {
                System.err.println("Database Connection Error: " + e.getMessage());
            }
        }
        return connection;
    }
}
