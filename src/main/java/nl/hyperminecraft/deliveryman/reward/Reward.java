package nl.hyperminecraft.deliveryman.reward;

import nl.hyperminecraft.deliveryman.profile.ClaimableState;
import nl.hyperminecraft.deliveryman.profile.DeliveryPlayer;
import nl.hyperminecraft.deliveryman.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    public String[] requiredPermissions;
    public String id;
    public long cooldown;
    public String[] commands;

    public List<String> claimableLore;
    public String claimableDisplayName;
    public int claimableDurability, claimableAmount;
    public Material claimableMaterial;

    public List<String> claimedLore;
    public String claimedDisplayName;
    public int claimedDurability, claimedAmount;
    public Material claimedMaterial;

    public List<String> notAvailableLore;
    public int notAvailableDurability, notAvailableAmount;
    public String notAvailableDisplayName;
    public Material notAvailableMaterial;

    public Reward(String[] requiredPermissions, String id, long cooldown, String[] commands,
                  List<String> claimableLore, String claimableDisplayName,
                  int claimableDurability, int claimableAmount, Material claimableMaterial,
                  List<String> claimedLore, String claimedDisplayName, int claimedDurability,
                  int claimedAmount, Material claimedMaterial, List<String> notAvailableLore,
                  int notAvailableDurability, int notAvailableAmount, String notAvailableDisplayName,
                  Material notAvailableMaterial) {
        this.requiredPermissions = requiredPermissions;
        this.id = id;
        this.cooldown = cooldown;
        this.commands = commands;
        this.claimableLore = claimableLore;
        this.claimableDisplayName = claimableDisplayName;
        this.claimableDurability = claimableDurability;
        this.claimableAmount = claimableAmount;
        this.claimableMaterial = claimableMaterial;
        this.claimedLore = claimedLore;
        this.claimedDisplayName = claimedDisplayName;
        this.claimedDurability = claimedDurability;
        this.claimedAmount = claimedAmount;
        this.claimedMaterial = claimedMaterial;
        this.notAvailableLore =  notAvailableLore;
        this.notAvailableDurability = notAvailableDurability;
        this.notAvailableAmount = notAvailableAmount;
        this.notAvailableDisplayName = notAvailableDisplayName;
        this.notAvailableMaterial = notAvailableMaterial;
    }


    public ItemStack getItemByPlayer(DeliveryPlayer player){
        ClaimableState state = player.getClaimableState(this);
        if (state == ClaimableState.AVAILABLE){
            ItemStack is = new ItemStack(Material.GREEN_STAINED_GLASS);
            is.setAmount(1);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), claimableLore));
            meta.setDisplayName(ChatUtils.translate(claimableDisplayName, player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
            return is;
        }else if (state == ClaimableState.ALREADY_CLAIMED){
            String seconds = player.getReadableTimeRemaining(this);
            //{claim_next_remain}
            ItemStack is = new ItemStack(Material.RED_STAINED_GLASS);
            is.setAmount(1);
            ItemMeta meta = is.getItemMeta();

            ArrayList<String> parsed = new ArrayList<>();

            for (String s : claimedLore) parsed.add(s.replace("{claim_next_remain}", seconds));

            meta.setLore(ChatUtils.translateAll(player.getPlayer(), parsed));
            meta.setDisplayName(ChatUtils.translate(claimedDisplayName.replace("{claim_next_remain}", seconds), player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
            return is;
        }else{
            ItemStack is = new ItemStack(Material.STONE);
            is.setAmount(1);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), notAvailableLore));
            meta.setDisplayName(ChatUtils.translate(notAvailableDisplayName, player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
            return is;
        }
    }

    public void setMetaByPlayer(DeliveryPlayer player, ItemStack is){
        ClaimableState state = player.getClaimableState(this);
        if (state == ClaimableState.AVAILABLE ){
            is.setType(claimableMaterial);
            is.setAmount(claimableAmount);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), claimableLore));
            meta.setDisplayName(ChatUtils.translate(claimableDisplayName, player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
            return;
        }else if(state == ClaimableState.ALREADY_CLAIMED){
            String seconds = player.getReadableTimeRemaining(this);
            //{claim_next_remain}
            is.setType(claimedMaterial);
            is.setAmount(claimedAmount);
            ItemMeta meta = is.getItemMeta();

            ArrayList<String> parsed = new ArrayList<>();

            for (String s : claimedLore) parsed.add(s.replace("{claim_next_remain}", seconds));

            meta.setLore(ChatUtils.translateAll(player.getPlayer(), parsed));
            meta.setDisplayName(ChatUtils.translate(claimedDisplayName.replace("{claim_next_remain}", seconds), player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
        }else{
            is.setType(notAvailableMaterial);
            is.setAmount(notAvailableAmount);
            ItemMeta meta = is.getItemMeta();
            meta.setLore(ChatUtils.translateAll(player.getPlayer(), notAvailableLore));
            meta.setDisplayName(ChatUtils.translate(notAvailableDisplayName, player.getPlayer()));
            meta.getPersistentDataContainer().set(RewardManager.getInstance().rewardKey, PersistentDataType.STRING, id);
            is.setItemMeta(meta);
        }
    }
}
