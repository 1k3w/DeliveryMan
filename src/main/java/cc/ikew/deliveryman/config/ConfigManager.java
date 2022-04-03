package cc.ikew.deliveryman.config;

import cc.ikew.deliveryman.Deliveryman;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    public static FileConfiguration messages, rewards, menus, config;

    static {
        Deliveryman dm = Deliveryman.instance;
        config = dm.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "messages.yml"));
        rewards = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "rewards.yml"));
        menus = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "menus.yml"));
    }

    public static void reloadConfigs(){
        Deliveryman dm = Deliveryman.instance;
        config = dm.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "messages.yml"));
        rewards = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "rewards.yml"));
        menus = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "menus.yml"));
    }
}
