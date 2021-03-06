package cc.ikew.deliveryman.MySql;

import cc.ikew.deliveryman.Deliveryman;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;


public class DataHandler {
    public static void createDataTable(){
        try {
            MySql.conn.createStatement().execute("CREATE TABLE IF NOT EXISTS `player_data` ( `uuid` VARCHAR(36) NOT NULL , `reward` VARCHAR(200) NOT NULL , `claimed_at` BIGINT UNSIGNED NOT NULL )");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public static void addDeliveryPlayer(UUID uuid){
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    PreparedStatement ps =  MySql.conn.prepareStatement("SELECT * FROM player_data WHERE uuid=?;");
                    ps.setString(1, uuid.toString());
                    ResultSet rs = ps.executeQuery();
                    HashMap<String, Long> data = new HashMap<>();
                    while(rs.next()){
                        data.put(rs.getString("reward"), rs.getLong("claimed_at"));
                    }
                    addDeliveryPlayerSync(uuid, data);
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                    addDeliveryPlayerSync(uuid, new HashMap<String, Long>());
                }
            }
        }.runTaskAsynchronously(Deliveryman.instance);
    }

    private static void addDeliveryPlayerSync(UUID uuid, HashMap<String, Long> data){
        new BukkitRunnable(){
            @Override
            public void run() {
                DeliveryPlayer.addDeliveryPlayer(new DeliveryPlayer(Bukkit.getPlayer(uuid), data));
                ////System.out.println("added Data!");
            }
        }.runTask(Deliveryman.instance);
    }

    public static void setRedeemed(UUID uuid, String reward, long claimedAt, DeliveryPlayer player){
        boolean claimed = player.hasClaimed(reward);
        new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    String query = null;
                    if (claimed){
                        query = "UPDATE player_data SET `claimed_at` = %c WHERE `uuid` = '%u' AND `reward` = '%r';";
                    }else{
                        query = "INSERT INTO player_data(`uuid`, `claimed_at`, `reward`) VALUES ('%u', %c, '%r');";
                    }
                    query = query.replace("%u", uuid.toString()).replace("%r", reward).replace("%c", claimedAt + "");
                    MySql.conn.createStatement().execute(query);
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Deliveryman.instance);
    }
}
