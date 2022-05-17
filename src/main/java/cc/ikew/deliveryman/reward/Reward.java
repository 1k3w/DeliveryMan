package cc.ikew.deliveryman.reward;

import cc.ikew.deliveryman.profile.ClaimableState;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    public String[] conditions;
    public String id;
    public long cooldown;
    public String[] commands;

    public List<String> claimableLore;
    public String claimableDisplayName;
    public int claimableAmount, claimableCustomData;
    public Material claimableMaterial;

    public List<String> claimedLore;
    public String claimedDisplayName;
    public int claimedAmount, claimedCustomData;
    public Material claimedMaterial;

    public List<String> notAvailableLore;
    public int notAvailableAmount, notAvailableCustomData;
    public String notAvailableDisplayName;
    public Material notAvailableMaterial;

    public Sound claimSound, unavailableSound, needConditionSound;

    public Reward(String configSection, FileConfiguration config, String id) {
        this.id = id;
        cooldown = config.getLong(configSection+".time");
        commands = config.getStringList(configSection + ".cmds").toArray(new String[0]);
        conditions = config.getStringList(configSection + ".conditions").toArray(new String[0]);

        //Locked Item
        claimedMaterial = Material.valueOf(config.getString(configSection + ".locked.material").toUpperCase());
        claimedAmount = config.contains(configSection + ".locked.amount") ? config.getInt(configSection + ".locked.amount") : 1;
        claimedDisplayName = ChatUtils.translate(config.getString(configSection + ".locked.name"));
        claimedLore = ChatUtils.translateAll(config.getStringList(configSection + ".locked.lore").toArray(new String[0]));
        claimedCustomData= config.getInt(configSection + ".locked.custom-model-data");
        unavailableSound = config.contains(configSection + ".locked.click-sound") ?Sound.valueOf(config.getString(configSection + ".locked.click-sound").toUpperCase()) : null;

        //available Item
        claimableMaterial = Material.valueOf(config.getString(configSection + ".available.material").toUpperCase());
        claimableAmount = config.contains(configSection + ".available.amount") ? config.getInt(configSection + ".available.amount") : 1;
        claimableDisplayName = ChatUtils.translate(config.getString(configSection + ".available.name"));
        claimableLore = ChatUtils.translateAll(config.getStringList(configSection + ".available.lore").toArray(new String[0]));
        claimableCustomData= config.getInt(configSection + ".available.custom-model-data");
        claimSound = config.contains(configSection + ".available.click-sound") ? Sound.valueOf(config.getString(configSection + ".available.click-sound").toUpperCase()) : null;

        //need-condition Item
        notAvailableMaterial = Material.valueOf(config.getString(configSection + ".need-condition.material").toUpperCase());
        notAvailableAmount= config.contains(configSection + ".need-condition.amount") ? config.getInt(configSection + ".need-condition.amount") : 1;
        notAvailableDisplayName = ChatUtils.translate(config.getString(configSection + ".need-condition.name"));
        notAvailableLore = ChatUtils.translateAll(config.getStringList(configSection + ".need-condition.lore").toArray(new String[0]));
        notAvailableCustomData = config.getInt(configSection + ".need-condition.custom-model-data");
        needConditionSound = config.contains(configSection + ".need-condition.click-sound") ? Sound.valueOf(config.getString(configSection + ".need-condition.click-sound").toUpperCase()) : null;
    }




    public ItemStack getItemByPlayer(DeliveryPlayer player){
        ClaimableState state = player.getClaimableState(this);
        if (state == ClaimableState.AVAILABLE){
            ItemStack is = new ItemStack(claimableMaterial, claimableAmount);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), claimableLore));
            meta.setDisplayName(ChatUtils.translate(claimableDisplayName, player.getPlayer()));
            if(claimableCustomData != -1) meta.setCustomModelData(claimableCustomData);
            is.setItemMeta(meta);

            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);

            return nbti.getItem();
        }else if (state == ClaimableState.ALREADY_CLAIMED){
            String seconds = player.getReadableTimeRemaining(this);
            ItemStack is = new ItemStack(claimedMaterial, claimedAmount);
            ItemMeta meta = is.getItemMeta();

            ArrayList<String> parsed = new ArrayList<>();

            for (String s : claimedLore) parsed.add(s.replace("{claim_next_remain}", seconds));

            meta.setLore(ChatUtils.translateAll(player.getPlayer(), parsed));
            meta.setDisplayName(ChatUtils.translate(claimedDisplayName.replace("{claim_next_remain}", seconds), player.getPlayer()));
            if(claimedCustomData != -1) meta.setCustomModelData(claimedCustomData);
            is.setItemMeta(meta);
            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);
            return nbti.getItem();
        }else{
            ItemStack is = new ItemStack(notAvailableMaterial, notAvailableAmount);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), notAvailableLore));
            meta.setDisplayName(ChatUtils.translate(notAvailableDisplayName, player.getPlayer()));
            if(notAvailableCustomData != -1) meta.setCustomModelData(notAvailableCustomData);
            is.setItemMeta(meta);
            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);
            return nbti.getItem();
        }
    }
}
