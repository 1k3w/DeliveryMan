package cc.ikew.deliveryman.menu;

import cc.ikew.deliveryman.menu.cosmetic.Cosmetic;
import cc.ikew.deliveryman.menu.cosmetic.CosmeticsHandler;
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
import java.util.List;

public class RewardMenu {

    private ItemStack fillIs;
    private HashMap<String, List<Integer>> rewardSlot;
    private HashMap<String, List<Integer>> cosmeticsSlot;
    private String title;
    private String name;
    private int rows;

    public HashMap<Player, Inventory> viewers = new HashMap<>();

    public void open(DeliveryPlayer player){
        Inventory inv = Bukkit.createInventory(null, rows * 9, ChatUtils.translate(title, player.getPlayer()));
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, fillIs);

        cosmeticsSlot.forEach((cosmeticName, slot) -> {
            Cosmetic cm = CosmeticsHandler.getInstance().getByID(cosmeticName);
            if (cm == null) {
                System.out.println("REWARD IS NULL!");
            }else{
                ItemStack is = cm.getByPlayer(player.getPlayer());
                for (int i : slot){
                    inv.setItem(i, is);
                }

            }

        });

        rewardSlot.forEach((rewardName, slot) -> {
            Reward r = RewardManager.getInstance().getReward(rewardName);
            if (r == null) {
                System.out.println("REWARD IS NULL!");
            }else{
                ItemStack is = r.getItemByPlayer(player);
                for (int i : slot){
                    inv.setItem(i, is);
                }

            }

        });

        viewers.put(player.getPlayer(), inv);
        player.getPlayer().openInventory(inv);
    }

    public RewardMenu(ItemStack fillIs, HashMap<String, List<Integer>> rewardSlot, String title, String name, int rows, HashMap<String, List<Integer>> cosmetics) {
        this.fillIs = fillIs;
        this.rewardSlot = rewardSlot;
        this.title = title;
        this.name = name;
        this.rows = rows;
        this.cosmeticsSlot = cosmetics;
    }

    public void onTick(){
        viewers.forEach((player, inv) ->{
            cosmeticsSlot.forEach((cosmeticName, slot) -> {
                Cosmetic cm = CosmeticsHandler.getInstance().getByID(cosmeticName);
                if (cm == null) {
                    System.out.println("REWARD IS NULL!");
                }else{
                    ItemStack is = cm.getByPlayer(player.getPlayer());
                    for (int i : slot){
                        inv.setItem(i, is);
                    }

                }

            });
            rewardSlot.forEach((name, slot) -> {
                Reward rw = RewardManager.getInstance().getReward(name);
                if (rw == null) {
                    System.out.println("REWARD IS NULL!");
                }else{
                    ItemStack is = rw.getItemByPlayer(DeliveryPlayer.getDeliveryPlayer(player));
                    NBTItem nbti = new NBTItem(is);
                    for (int i : slot){
                        inv.setItem(i, is);
                    }

                }
            });
        });
    }

    public ItemStack getFillIs() {
        return fillIs;
    }

    public HashMap<String, List<Integer>> getRewardSlot() {
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
            Player p = (Player) e.getWhoClicked();
            if (!viewers.containsKey(p)) return;
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null) return;
            NBTItem nbti = new NBTItem(clicked);
            if (nbti.hasKey(RewardManager.getInstance().rewardKey)){
                String rewardType = nbti.getString(RewardManager.getInstance().rewardKey);
                Reward reward = RewardManager.getInstance().getReward(rewardType);
                DeliveryPlayer dp = DeliveryPlayer.getDeliveryPlayer(p);
                dp.claimReward(reward);
            }
            if(nbti.hasKey(CosmeticsHandler.cosmeticKey)){
                Cosmetic cm = CosmeticsHandler.getInstance().getByID(nbti.getString(CosmeticsHandler.cosmeticKey));
                if (cm != null){
                    for (String s : cm.commands){
                        if (s.startsWith("[c]")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("[c]", "")
                                .replace("{player}", p.getName()).replace("{name}", p.getName()));
                        if (s.startsWith("[m]"))
                            p.sendMessage(ChatUtils.translate(s.replace("[m]", "")
                                    .replace("{player}", p.getName()).replace("{name}", p.getName()), p));
                    }
                }
            }
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
