package cc.ikew.deliveryman.config;

import cc.ikew.deliveryman.Deliveryman;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    public static FileConfiguration messages, items, menus, config;
    public static final File messagesFile, itemsFile, menuFile;

    static {
        Deliveryman dm = Deliveryman.instance;
        messagesFile = new File(dm.getDataFolder(), "messages.yml");
        itemsFile = new File(dm.getDataFolder(), "items.yml");
        menuFile = new File(dm.getDataFolder(), "menus.yml");
        config = dm.getConfig();
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        items = YamlConfiguration.loadConfiguration(itemsFile);
        menus = YamlConfiguration.loadConfiguration(menuFile);
    }

    public static void reloadConfigs(){
        Deliveryman dm = Deliveryman.instance;
        config = dm.getConfig();
        messages = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "messages.yml"));
        items = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "items.yml"));
        menus = YamlConfiguration.loadConfiguration(new File(dm.getDataFolder(), "menus.yml"));
    }
}
