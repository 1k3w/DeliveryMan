package cc.ikew.deliveryman;

import cc.ikew.deliveryman.MySql.DataHandler;
import cc.ikew.deliveryman.MySql.MySql;
import cc.ikew.deliveryman.hooks.bstats.Metrics;
import cc.ikew.deliveryman.hooks.papi.PAPIDataHandler;
import cc.ikew.deliveryman.hooks.papi.PAPIHook;
import cc.ikew.deliveryman.hooks.vault.VaultHook;
import cc.ikew.deliveryman.listener.RegisterListener;
import cc.ikew.deliveryman.command.DeliveryCommand;
import cc.ikew.deliveryman.menu.RewardMenuHandler;
import cc.ikew.deliveryman.reward.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class Deliveryman extends JavaPlugin {

    public static Deliveryman instance;
    public Connection conn;
    public FileConfiguration rewardsConfig, messagesConfig;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        loadFiles();
        conn = MySql.connect();
        getServer().getPluginManager().registerEvents(RewardMenuHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(new RegisterListener(), this);
        RewardMenuHandler.getInstance().load(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "menus.yml")));
        RewardManager.getInstance().load(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "items.yml")));
        getServer().getPluginCommand("deliveryman").setExecutor(new DeliveryCommand());
        DataHandler.createDataTable();
        enableHooks();
        Metrics metrics = new Metrics(this, 14818);

        new BukkitRunnable(){

            @Override
            public void run() {
                try {
                    if (MySql.conn.isClosed()){
                        MySql.connect();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimer(Deliveryman.instance, 20 * 60, 20 * 60);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MySql.closeConnection(conn);
    }

    private void loadFiles() {
        saveResource("items.yml", false);
        saveResource("messages.yml", false);
        saveResource("menus.yml", false);
    }

    private void enableHooks(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            PAPIDataHandler.enable();
            new PAPIHook().register();
        }
        VaultHook.setupEconomy(this);
    }
}
