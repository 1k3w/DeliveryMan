package cc.ikew.deliveryman.reward;

import cc.ikew.deliveryman.profile.ClaimableState;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    public String[] requiredPermissions;
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

    public Reward(String[] requiredPermissions, String id, long cooldown, String[] commands,
                  List<String> claimableLore, String claimableDisplayName,
                  int claimableAmount, Material claimableMaterial,
                  List<String> claimedLore, String claimedDisplayName,
                  int claimedAmount, Material claimedMaterial, List<String> notAvailableLore,
                  int notAvailableAmount, String notAvailableDisplayName,
                  Material notAvailableMaterial,
                  int availableCustomData, int lockedCustomData, int needConditionCustomData) {
        this.requiredPermissions = requiredPermissions;
        this.id = id;
        this.cooldown = cooldown;
        this.commands = commands;
        this.claimableLore = claimableLore;
        this.claimableDisplayName = claimableDisplayName;
        this.claimableAmount = claimableAmount;
        this.claimableMaterial = claimableMaterial;
        this.claimedLore = claimedLore;
        this.claimedDisplayName = claimedDisplayName;
        this.claimedAmount = claimedAmount;
        this.claimedMaterial = claimedMaterial;
        this.notAvailableLore =  notAvailableLore;
        this.notAvailableAmount = notAvailableAmount;
        this.notAvailableDisplayName = notAvailableDisplayName;
        this.notAvailableMaterial = notAvailableMaterial;
        this.claimableCustomData = availableCustomData;
        this.claimedCustomData = lockedCustomData;
        this.notAvailableCustomData = needConditionCustomData;
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
            //System.out.println(is.getAmount());
            //System.out.println(is.getType().name());
            return nbti.getItem();
        }else if (state == ClaimableState.ALREADY_CLAIMED){
            String seconds = player.getReadableTimeRemaining(this);
            //{claim_next_remain}
            ItemStack is = new ItemStack(notAvailableMaterial, claimedAmount);
            ItemMeta meta = is.getItemMeta();

            ArrayList<String> parsed = new ArrayList<>();

            for (String s : claimedLore) parsed.add(s.replace("{claim_next_remain}", seconds));

            meta.setLore(ChatUtils.translateAll(player.getPlayer(), parsed));
            meta.setDisplayName(ChatUtils.translate(claimedDisplayName.replace("{claim_next_remain}", seconds), player.getPlayer()));
            if(claimedCustomData != -1) meta.setCustomModelData(claimedCustomData);
            //System.out.println(is.getAmount());
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
            //System.out.println(is.getAmount());
            is.setItemMeta(meta);
            NBTItem nbti = new NBTItem(is);
            nbti.setString(RewardManager.getInstance().rewardKey, id);
            return nbti.getItem();
        }
    }
}
