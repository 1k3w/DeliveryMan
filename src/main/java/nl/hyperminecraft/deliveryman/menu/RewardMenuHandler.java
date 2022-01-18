package nl.hyperminecraft.deliveryman.menu;

import nl.hyperminecraft.deliveryman.Deliveryman;
import nl.hyperminecraft.deliveryman.config.ConfigManager;
import nl.hyperminecraft.deliveryman.profile.DeliveryPlayer;
import nl.hyperminecraft.deliveryman.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class RewardMenuHandler implements Listener {
    private static RewardMenuHandler instance;

    public static RewardMenuHandler getInstance(){
        if (instance == null) instance = new RewardMenuHandler();
        return instance;
    }

    public RewardMenuHandler(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for (RewardMenu menu : menus) menu.onTick();
            }
        }.runTaskTimer(Deliveryman.instance, 10, 10);
    }

    private ArrayList<RewardMenu> menus = new ArrayList<>();

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        for (RewardMenu menu : menus) menu.onLeave(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e){ for (RewardMenu menu : menus) menu.onClose(e);}

    @EventHandler
    public void onClick(InventoryClickEvent e){
        for (RewardMenu menu : menus) menu.onClick(e);
    }

    public void openMenu(Player player, String menuType){
        for (RewardMenu menu : menus){
            if (menu.getName().equalsIgnoreCase(menuType)){
                menu.open(DeliveryPlayer.getDeliveryPlayer(player));
                return;
            }
        }
        player.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("menu-not-found").replace("{menu}", menuType), player));
    }

    public void load(FileConfiguration config){
        menus = new ArrayList<>();
        for (String s : config.getConfigurationSection("menus").getKeys(false)){
            ItemStack fillItem = new ItemStack(Material.valueOf(config.getString("menus." + s + ".fill-item.material")));
            ItemMeta meta = fillItem.getItemMeta();
            meta.setDisplayName(ChatUtils.translate(config.getString("menus." + s + ".fill-item.name")));
            if (config.getBoolean("menus." + s + ".fill-item.glint")){
                meta.addEnchant(Enchantment.DURABILITY, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
            }
            meta.setLore(ChatUtils.translateAll(config.getStringList("menus." + s + ".fill-item.lore").toArray(new String[0])));
            fillItem.setItemMeta(meta);
            HashMap<String, Integer> rewardSlots = new HashMap<>();
            for (String rewardName : config.getConfigurationSection("menus." + s + ".rewards").getKeys(false)){
                rewardSlots.put(rewardName, config.getInt("menus." + s + ".rewards." + rewardName));
            }

            RewardMenu menu = new RewardMenu(fillItem, rewardSlots,config.getString("menus." + s + ".title"), config.getString("menus." + s + ".name"), config.getInt("menus." + s + ".rows"));
            menus.add(menu);
        }
    }
}
