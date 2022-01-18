package nl.hyperminecraft.deliveryman;

import nl.hyperminecraft.deliveryman.MySql.DataHandler;
import nl.hyperminecraft.deliveryman.MySql.MySql;
import nl.hyperminecraft.deliveryman.command.DeliveryCommand;
import nl.hyperminecraft.deliveryman.listener.RegisterListener;
import nl.hyperminecraft.deliveryman.menu.RewardMenuHandler;
import nl.hyperminecraft.deliveryman.reward.RewardManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;

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
        saveResource("menus.yml", false);
        saveResource("rewards.yml", false);
        saveResource("messages.yml", false);
        RewardMenuHandler.getInstance().load(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "menus.yml")));
        RewardManager.getInstance().load(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "rewards.yml")));
        getServer().getPluginCommand("deliveryman").setExecutor(new DeliveryCommand());
        DataHandler.createDataTable();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        MySql.closeConnection(conn);
    }

    private void loadFiles() {
        saveResource("rewards.yml", false);
        saveResource("messages.yml", false);
        saveResource("menus.yml", false);
    }
}
