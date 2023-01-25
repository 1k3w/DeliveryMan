package cc.ikew.deliveryman.reward;

import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.config.Configgable;
import cc.ikew.deliveryman.profile.ClaimableState;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    public Configgable<List<String>> conditions;
    public String id;
    public Configgable<Long> cooldown;
    public Configgable<List<String>> commands;

    public Configgable<List<String>> claimableLore;
    public Configgable<String> claimableDisplayName;
    public Configgable<Integer> claimableAmount, claimableCustomData;
    public Configgable<String> claimableMaterial;

    public Configgable<List<String>> claimedLore;
    public Configgable<String> claimedDisplayName;
    public Configgable<Integer> claimedAmount, claimedCustomData;
    public Configgable<String> claimedMaterial;

    public Configgable<List<String>> notAvailableLore;
    public Configgable<Integer> notAvailableAmount, notAvailableCustomData;
    public Configgable<String> notAvailableDisplayName;
    public Configgable<String> notAvailableMaterial;

    public Configgable<String> claimSound, unavailableSound, needConditionSound;

    public Reward(String configSection, FileConfiguration config, String id) {
        this.id = id;
        cooldown = new Configgable<>(configSection+".time", config, ConfigManager.itemsFile);
        commands = new Configgable<>( configSection+ ".cmds", config, ConfigManager.itemsFile);
        conditions = new Configgable<>(configSection + ".conditions", config, ConfigManager.itemsFile);

        //Locked Item
        claimedMaterial = new Configgable<>(configSection + ".locked.material", config, ConfigManager.itemsFile);
        claimedAmount = new Configgable<>(configSection + ".locked.amount", config, ConfigManager.itemsFile);
        claimedDisplayName = new Configgable<>(configSection + ".locked.name", config, ConfigManager.itemsFile);
        claimedLore = new Configgable<>(configSection + ".locked.lore", config, ConfigManager.itemsFile);
        claimedCustomData = new Configgable<>(configSection + ".locked.custom-model-data", config, ConfigManager.itemsFile);
        unavailableSound = new Configgable<>(configSection + ".locked.click-sound", config, ConfigManager.itemsFile);

        //available Item
        claimableMaterial = new Configgable<>(configSection + ".available.material", config, ConfigManager.itemsFile);
        claimableAmount = new Configgable<>(configSection + ".available.amount", config, ConfigManager.itemsFile);
        claimableDisplayName = new Configgable<>(configSection + ".available.name", config, ConfigManager.itemsFile);
        claimableLore = new Configgable<>(configSection + ".available.lore", config, ConfigManager.itemsFile);
        claimableCustomData= new Configgable<>(configSection + ".available.custom-model-data", config, ConfigManager.itemsFile);
        claimSound = new Configgable<>(configSection + ".available.click-sound", config, ConfigManager.itemsFile);

        //need-condition Item
        notAvailableMaterial = new Configgable<>(configSection + ".need-condition.material", config, ConfigManager.itemsFile);
        notAvailableAmount= new Configgable<>(configSection + ".need-condition.amount", config, ConfigManager.itemsFile);
        notAvailableDisplayName = new Configgable<>(configSection + ".need-condition.name", config, ConfigManager.itemsFile);
        notAvailableLore = new Configgable<>(configSection + ".need-condition.lore", config, ConfigManager.itemsFile);
        notAvailableCustomData = new Configgable<>(configSection + ".need-condition.custom-model-data", config, ConfigManager.itemsFile);
        needConditionSound = new Configgable<>(configSection + ".need-condition.click-sound", config, ConfigManager.itemsFile);
    }




    public ItemStack getItemByPlayer(DeliveryPlayer player){
        ClaimableState state = player.getClaimableState(this);
        if (state == ClaimableState.AVAILABLE){
            ItemStack is = new ItemStack(Material.valueOf( claimableMaterial.get().toUpperCase()), claimableAmount.get());
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), claimableLore.get()));
            meta.setDisplayName(ChatUtils.translate(claimableDisplayName.get(), player.getPlayer()));
            if(claimableCustomData.get() != -1) meta.setCustomModelData(claimableCustomData.get());
            is.setItemMeta(meta);

            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);

            return nbti.getItem();
        }else if (state == ClaimableState.ALREADY_CLAIMED){
            String seconds = player.getReadableTimeRemaining(this);
            ItemStack is = new ItemStack(Material.valueOf( claimedMaterial.get().toUpperCase()), claimedAmount.get());
            ItemMeta meta = is.getItemMeta();

            ArrayList<String> parsed = new ArrayList<>();

            for (String s : claimedLore.get()) parsed.add(s.replace("{claim_next_remain}", seconds));

            meta.setLore(ChatUtils.translateAll(player.getPlayer(), parsed));
            meta.setDisplayName(ChatUtils.translate(claimedDisplayName.get().replace("{claim_next_remain}", seconds), player.getPlayer()));
            if(claimedCustomData.get() != -1) meta.setCustomModelData(claimedCustomData.get());
            is.setItemMeta(meta);
            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);
            return nbti.getItem();
        }else{
            ItemStack is = new ItemStack(Material.valueOf(notAvailableMaterial.get().toUpperCase()), notAvailableAmount.get());
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), notAvailableLore.get()));
            meta.setDisplayName(ChatUtils.translate(notAvailableDisplayName.get(), player.getPlayer()));
            if(notAvailableCustomData.get() != -1) meta.setCustomModelData(notAvailableCustomData.get());
            is.setItemMeta(meta);
            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);
            return nbti.getItem();
        }
    }
}
