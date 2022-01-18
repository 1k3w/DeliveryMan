package nl.hyperminecraft.deliveryman.reward;

import nl.hyperminecraft.deliveryman.profile.DeliveryPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class RewardItem {

    private String reward;

    public ArrayList<String> claimableLore;
    public String claimableDisplayName;
    public Material claimableMaterial;

    public ArrayList<String> claimedLore;
    public String claimedDisplayName;
    public Material claimedMaterial;

    public ArrayList<String> notAvailableLore;
    public String notAvailableDisplayName;
    public Material notAvailableMaterial;

    public RewardItem(ArrayList<String> claimableLore,
                      String claimableDisplayName,
                      Material claimableMaterial,
                      ArrayList<String> claimedLore,
                      String claimedDisplayName,
                      Material claimedMaterial,
                      ArrayList<String> notAvailableLore,
                      String notAvailableDisplayName,
                      Material notAvailableMaterial) {
        this.claimableLore = claimableLore;
        this.claimableDisplayName = claimableDisplayName;
        this.claimableMaterial = claimableMaterial;
        this.claimedLore = claimedLore;
        this.claimedDisplayName = claimedDisplayName;
        this.claimedMaterial = claimedMaterial;
        this.notAvailableLore = notAvailableLore;
        this.notAvailableDisplayName = notAvailableDisplayName;
        this.notAvailableMaterial = notAvailableMaterial;
    }

    public ItemStack getItemByPlayer(DeliveryPlayer p){
        return null;
    }


    public ItemMeta getMetaByPlayer(DeliveryPlayer p){
        return null;
    }
}
