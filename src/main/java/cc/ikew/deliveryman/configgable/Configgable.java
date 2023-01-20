package cc.ikew.deliveryman.configgable;

import cc.ikew.deliveryman.Deliveryman;
import cc.ikew.deliveryman.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class Configgable <T>{
    private String path;
    private FileConfiguration configToUse;
    private File file; // file of the config.

    public Configgable(String path, FileConfiguration configToUse, File configFile) {
        this.path = path;
        this.configToUse = configToUse;
        file = configFile;
    }

    public T get(){
        return (T) configToUse.get(path);
    }

    public T getOrDefault(T def){
        return (T) configToUse.get(path, def);
    }

    public void save(T val){
        configToUse.set(path, val);
        Bukkit.getScheduler().runTaskAsynchronously(Deliveryman.instance, () -> {
            try {
                configToUse.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getKey(){
        return path.split(".")[path.split(".").length - 1];
    }
}
