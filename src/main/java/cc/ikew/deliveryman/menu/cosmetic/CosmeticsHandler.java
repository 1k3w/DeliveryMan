package cc.ikew.deliveryman.menu.cosmetic;

import cc.ikew.deliveryman.config.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;


import java.util.HashMap;

public class CosmeticsHandler {

    public static final String cosmeticKey = "cosmeticID";

    private HashMap<String, Cosmetic> cosmetics = new HashMap<>();

    private static CosmeticsHandler instance;


    public static CosmeticsHandler getInstance(){
        if (instance == null) instance = new CosmeticsHandler();
        return instance;
    }

    public CosmeticsHandler(){
        load();
    }

    public void load() {
        FileConfiguration itemsConfig = ConfigManager.items;
        for(String key : itemsConfig.getConfigurationSection("cosmetics").getKeys(false)){
            cosmetics.put(key, new Cosmetic( "cosmetics." + key));
        }
    }

    public Cosmetic getByID(String id){
        return cosmetics.get(id);
    }

}
