package cc.ikew.deliveryman.reward;

import cc.ikew.deliveryman.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public class RewardManager {

    private static RewardManager instance;

    public static RewardManager getInstance(){
        if (instance == null) instance = new RewardManager();
        return instance;
    }

    public HashMap<String, Reward> rewards = new HashMap<>();

    public final String rewardKey = "reward";


    public void load(FileConfiguration config){
        rewards = new HashMap<>();
        for (String s : config.getConfigurationSection("rewards").getKeys(false)){
            rewards.put(s, new Reward("rewards." + s, config, s));
        }
    }

    public Reward getReward(String name){
        return rewards.get(name);
    }
}
