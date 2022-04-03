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

    public String rewardKey = "reward";

    //laad alle items in
    public void load(FileConfiguration config){
        rewards = new HashMap<>();
        for (String s : config.getConfigurationSection("rewards").getKeys(false)){
            long cooldown = config.getLong("rewards."+s+".time");
            String[] commands = config.getStringList("rewards." + s + ".cmds").toArray(new String[0]);
            String[] conditions = config.getStringList("rewards." + s + ".conditions").toArray(new String[0]);

            //Locked Item
            Material lockedMaterial = Material.valueOf(config.getString("rewards." + s + ".locked.material").toUpperCase());
            int lockedAmount = config.contains("rewards." + s + ".locked.amount") ? config.getInt("rewards." + s + ".locked.amount") : 1;
            String lockedName = ChatUtils.translate(config.getString("rewards." + s + ".locked.name"));
            List<String> lockedLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".locked.lore").toArray(new String[0]));
            int lockedCustomData = config.getInt("rewards." + s + ".locked.custom-model-data");

            //available Item
            Material availableMaterial = Material.valueOf(config.getString("rewards." + s + ".available.material").toUpperCase());
            int availableAmount = config.contains("rewards." + s + ".available.amount") ? config.getInt("rewards." + s + ".available.amount") : 1;
            String availableName = ChatUtils.translate(config.getString("rewards." + s + ".available.name"));
            List<String> availableLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".available.lore").toArray(new String[0]));
            int availableCustomData = config.getInt("rewards." + s + ".available.custom-model-data");

            //need-condition Item
            Material needConditionMaterial = Material.valueOf(config.getString("rewards." + s + ".need-condition.material").toUpperCase());
            int needConditionAmount = config.contains("rewards." + s + ".need-condition.amount") ? config.getInt("rewards." + s + ".need-condition.amount") : 1;
            String needConditionName = ChatUtils.translate(config.getString("rewards." + s + ".need-condition.name"));
            List<String> needConditionLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".need-condition.lore").toArray(new String[0]));
            int needConditionCustomData = config.getInt("rewards." + s + ".need-condition.custom-model-data");
            rewards.put(s, new Reward(conditions, s, cooldown, commands, availableLore, availableName, availableAmount, availableMaterial,
                    lockedLore, lockedName, lockedAmount, lockedMaterial, needConditionLore, needConditionAmount,
                    needConditionName, needConditionMaterial, availableCustomData, lockedCustomData, needConditionCustomData));
        }
    }

    public Reward getReward(String name){
        return rewards.get(name);
    }
}
