package cc.ikew.deliveryman.MySql;

import cc.ikew.deliveryman.Deliveryman;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySql {

    public static Connection conn;

    public static Connection connect() {
        String url = Deliveryman.instance.getConfig().getString("mysql.dsn");
        String username = Deliveryman.instance.getConfig().getString("mysql.username");
        String password = Deliveryman.instance.getConfig().getString("mysql.password");
        boolean sqlite = Deliveryman.instance.getConfig().getBoolean("mysql.sqlite");

        try {
            if(!sqlite){
                Connection connection = DriverManager.getConnection(url, username, password);
                connection.setAutoCommit(true);
                conn = connection;
            }else{
                File db = new File(Deliveryman.instance.getDataFolder(), "data.db");
                if (!db.exists()) db.createNewFile();
                conn = DriverManager.getConnection("jdbc:sqlite:plugins/" + Deliveryman.instance.getDataFolder().getName() + "/data.db");
            }

            return conn;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
