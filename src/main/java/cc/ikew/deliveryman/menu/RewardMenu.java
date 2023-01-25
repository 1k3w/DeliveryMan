package cc.ikew.deliveryman.menu;

import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.config.Configgable;
import cc.ikew.deliveryman.menu.cosmetic.Cosmetic;
import cc.ikew.deliveryman.menu.cosmetic.CosmeticsHandler;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import cc.ikew.deliveryman.reward.Reward;
import cc.ikew.deliveryman.reward.RewardManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RewardMenu {


    private final String basePath;
    private final FileConfiguration config;

    private Configgable<String> fillIsMaterial;
    private Configgable<String> fillIsName;
    private Configgable<Boolean> fillIsGlint;
    private Configgable<List<String>> fillIsLore;


    private List<Configgable<List<Integer>>> rewardSlot;
    private List<Configgable<List<Integer>>> cosmeticsSlot;
    private Configgable<String> title;
    private Configgable<Integer> rows;

    public HashMap<Player, Inventory> viewers = new HashMap<>();

    public void open(DeliveryPlayer player){
        Inventory inv = Bukkit.createInventory(null, rows.get() * 9, ChatUtils.translate(title.getOrDefault(""), player.getPlayer()));
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, getFillIS(player.getPlayer()));

        cosmeticsSlot.forEach((cosmeticName) -> {
            Cosmetic cm = CosmeticsHandler.getInstance().getByID(cosmeticName.getKey());
            if (cm == null) {
                System.out.println("REWARD IS NULL!");
            }else{
                ItemStack is = cm.getByPlayer(player.getPlayer());
                for (int i : cosmeticName.get()){
                    inv.setItem(i, is);
                }

            }

        });

        rewardSlot.forEach((rewardName) -> {
            Reward r = RewardManager.getInstance().getReward(rewardName.getKey());
            if (r == null) {
                System.out.println("REWARD IS NULL!");
            }else{
                ItemStack is = r.getItemByPlayer(player);
                for (int i : rewardName.get()){
                    inv.setItem(i, is);
                }
            }

        });

        viewers.put(player.getPlayer(), inv);
        player.getPlayer().openInventory(inv);
    }

    //fillItem, rewardSlots,config.getString("menus." + s + ".title"), config.getString("menus." + s + ".name"), config.getInt("menus." + s + ".rows"), cosmeticSlots
    public RewardMenu(String basePath, FileConfiguration config) {
        this.basePath = basePath;
        this.config = config;
        this.rewardSlot = new ArrayList<>();

        for (String rewardName : config.getConfigurationSection(basePath + ".rewards").getKeys(false)){
            rewardSlot.add(new Configgable<>(basePath + ".rewards." + rewardName, config, ConfigManager.menuFile));
        }

        this.title = new Configgable<>(basePath + ".title", config, ConfigManager.menuFile);
        this.rows = new Configgable<>(basePath + ".rows", config, ConfigManager.menuFile);
        this.cosmeticsSlot = new ArrayList<>();

        for (String cosmeticName : config.getConfigurationSection(basePath + ".cosmetics").getKeys(false)){
            cosmeticsSlot.add(new Configgable<>(basePath + ".cosmetics." + cosmeticName, config, ConfigManager.menuFile));
        }

        fillIsMaterial = new Configgable<>(basePath + ".fill-item.material", config, ConfigManager.menuFile);
        fillIsGlint = new Configgable<>(basePath + ".fill-item.glint", config, ConfigManager.menuFile);
        fillIsLore = new Configgable<>(basePath + ".fill-item.lore", config, ConfigManager.menuFile);
        fillIsName = new Configgable<>(basePath + ".fill-item.name", config, ConfigManager.menuFile);


    }

    public void onTick(){
        viewers.forEach((player, inv) ->{
            cosmeticsSlot.forEach((cosmetic) -> {
                Cosmetic cm = CosmeticsHandler.getInstance().getByID(cosmetic.getKey());
                if (cm == null) {
                    System.out.println("REWARD IS NULL!");
                }else{
                    ItemStack is = cm.getByPlayer(player.getPlayer());
                    for (int i : cosmetic.get()){
                        inv.setItem(i, is);
                    }

                }

            });
            rewardSlot.forEach((reward) -> {
                Reward rw = RewardManager.getInstance().getReward(reward.getKey());
                if (rw == null) {
                    System.out.println("REWARD IS NULL!");
                }else{
                    ItemStack is = rw.getItemByPlayer(DeliveryPlayer.getDeliveryPlayer(player));
                    NBTItem nbti = new NBTItem(is);
                    for (int i : reward.get()){
                        inv.setItem(i, is);
                    }

                }
            });
        });
    }

    public List<Configgable<List<Integer>>> getRewardSlot() {
        return rewardSlot;
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


    public int getRows() {
        return rows.get();
    }

    public ItemStack getFillIS(Player p){
        ItemStack fillItem = new ItemStack(Material.valueOf(fillIsMaterial.getOrDefault("stone").toUpperCase()));
        ItemMeta meta = fillItem.getItemMeta();
        meta.setDisplayName(ChatUtils.translate(fillIsName.getOrDefault("")));
        if (fillIsGlint.getOrDefault(false)){
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        }
        meta.setLore(ChatUtils.translateAll(p,fillIsLore.getOrDefault(new ArrayList<>())));
        fillItem.setItemMeta(meta);
        return fillItem;
    }

    public String getName() {
        return basePath.split(".")[basePath.split(".").length -1];
    }
}
