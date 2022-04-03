package cc.ikew.deliveryman.menu;

import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import cc.ikew.deliveryman.reward.Reward;
import cc.ikew.deliveryman.reward.RewardManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RewardMenu {

    private ItemStack fillIs;
    private HashMap<String, Integer> rewardSlot;
    private String title;
    private String name;
    private int rows;

    public HashMap<Player, Inventory> viewers = new HashMap<>();

    public void open(DeliveryPlayer player){
        Inventory inv = Bukkit.createInventory(null, rows * 9, ChatUtils.translate(title, player.getPlayer()));
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, fillIs);

        rewardSlot.forEach((rewardName, slot) -> {
            Reward r = RewardManager.getInstance().getReward(rewardName);
            if (r == null) {
                System.out.println("REWARD IS NULL!");
            }else{
                ItemStack is = r.getItemByPlayer(player);
                ////System.out.println(is.getType().name());

                inv.setItem(slot, is);
            }

        });

        viewers.put(player.getPlayer(), inv);
        player.getPlayer().openInventory(inv);
    }

    public RewardMenu(ItemStack fillIs, HashMap<String, Integer> rewardSlot, String title, String name, int rows) {
        this.fillIs = fillIs;
        this.rewardSlot = rewardSlot;
        this.title = title;
        this.name = name;
        this.rows = rows;
    }

    public void onTick(){
        viewers.forEach((player, inv) ->{
            rewardSlot.forEach((name, slot) -> {
                inv.setItem(slot, RewardManager.getInstance().getReward(name).getItemByPlayer(DeliveryPlayer.getDeliveryPlayer(player)));
                //RewardManager.getInstance().getReward(name).setMetaByPlayer(DeliveryPlayer.getDeliveryPlayer(player), inv.getItem(slot));
            });
        });
    }

    public ItemStack getFillIs() {
        return fillIs;
    }

    public HashMap<String, Integer> getRewardSlot() {
        return rewardSlot;
    }

    public void setFillIs(ItemStack fillIs) {
        this.fillIs = fillIs;
    }

    public void onClose(InventoryCloseEvent e){
        Player toremove = null;
        for (Inventory inv : viewers.values()) if (e.getInventory() == inv) toremove = (Player) e.getPlayer();
        if (toremove != null) viewers.remove(toremove);
    }

    public void onClick(InventoryClickEvent e){
        if (e.getWhoClicked() instanceof Player){
            //System.out.println(1);
            Player p = (Player) e.getWhoClicked();
            if (!viewers.containsKey(p)) return;
            //System.out.println(2);
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;
            //System.out.println(3);
            NBTItem nbti = new NBTItem(clicked);
            if (nbti.hasKey(RewardManager.getInstance().rewardKey)){
                String rewardType = nbti.getString(RewardManager.getInstance().rewardKey);
                Reward reward = RewardManager.getInstance().getReward(rewardType);
                DeliveryPlayer dp = DeliveryPlayer.getDeliveryPlayer(p);
                dp.claimReward(reward);
                return;
            }
            //System.out.println(4);
        }
    }

    public void onLeave(PlayerQuitEvent e){
        viewers.remove(e.getPlayer());
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }
}
