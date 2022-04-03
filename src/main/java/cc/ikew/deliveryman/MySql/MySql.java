package cc.ikew.deliveryman.MySql;

import cc.ikew.deliveryman.Deliveryman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySql {

    public static Connection conn;

    public static Connection connect() {
        String url = Deliveryman.instance.getConfig().getString("mysql.dsn");
        String username = Deliveryman.instance.getConfig().getString("mysql.username");
        String password = Deliveryman.instance.getConfig().getString("mysql.password");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            conn = connection;
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
