package cc.ikew.deliveryman.menu;

import cc.ikew.deliveryman.Deliveryman;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.utils.ChatUtils;
import cc.ikew.deliveryman.config.ConfigManager;
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
import java.util.List;

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
            RewardMenu menu = new RewardMenu("menus." + s,config);
            menus.add(menu);
        }
    }
}
