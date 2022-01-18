package nl.hyperminecraft.deliveryman.reward;

import me.clip.placeholderapi.libs.kyori.adventure.key.Namespaced;
import nl.hyperminecraft.deliveryman.Deliveryman;
import nl.hyperminecraft.deliveryman.profile.DeliveryPlayer;
import nl.hyperminecraft.deliveryman.utils.ChatUtils;
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

    private HashMap<String, Reward> rewards = new HashMap<>();

    public NamespacedKey rewardKey = NamespacedKey.fromString("reward");

    //laad alle items in
    public void load(FileConfiguration config){
        rewards = new HashMap<>();
        for (String s : config.getConfigurationSection("rewards").getKeys(false)){
            long cooldown = config.getLong("rewards."+s+".time");
            String[] commands = config.getStringList("rewards." + s + ".cmds").toArray(new String[0]);
            String[] conditions = config.getStringList("rewards." + s + ".conditions").toArray(new String[0]);

            //Locked Item
            Material lockedMaterial = Material.valueOf(config.getString("rewards." + s + ".locked.material").toUpperCase());
            int lockedDurability = config.contains("rewards." + s + ".locked.durability") ? config.getInt("rewards." + s + ".locked.durability") : -1;
            int lockedAmount = config.contains("rewards." + s + ".locked.amount") ? config.getInt("rewards." + s + ".locked.durability") : 1;
            String lockedName = ChatUtils.translate(config.getString("rewards." + s + ".locked.name"));
            List<String> lockedLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".locked.lore").toArray(new String[0]));

            //available Item
            Material availableMaterial = Material.valueOf(config.getString("rewards." + s + ".available.material").toUpperCase());
            int availableDurability = config.contains("rewards." + s + ".available.durability") ? config.getInt("rewards." + s + ".available.durability") : -1;
            int availableAmount = config.contains("rewards." + s + ".available.amount") ? config.getInt("rewards." + s + ".available.durability") : 1;
            String availableName = ChatUtils.translate(config.getString("rewards." + s + ".available.name"));
            List<String> availableLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".available.lore").toArray(new String[0]));

            //need-condition Item
            Material needConditionMaterial = Material.valueOf(config.getString("rewards." + s + ".need-condition.material").toUpperCase());
            int needConditionDurability = config.contains("rewards." + s + ".need-condition.durability") ? config.getInt("rewards." + s + ".need-condition.durability") : -1;
            int needConditionAmount = config.contains("rewards." + s + ".need-condition.amount") ? config.getInt("rewards." + s + ".need-condition.durability") : 1;
            String needConditionName = ChatUtils.translate(config.getString("rewards." + s + ".need-condition.name"));
            List<String> needConditionLore = ChatUtils.translateAll(config.getStringList("rewards." + s + ".need-condition.lore").toArray(new String[0]));
            rewards.put(s, new Reward(conditions, s, cooldown, commands, availableLore, availableName, availableDurability, availableAmount, availableMaterial,
                    lockedLore, lockedName, lockedDurability, lockedAmount, lockedMaterial, needConditionLore, needConditionDurability, needConditionAmount,
                    needConditionName, needConditionMaterial));
        }
    }

    public Reward getReward(String name){
        return rewards.get(name);
    }
}
