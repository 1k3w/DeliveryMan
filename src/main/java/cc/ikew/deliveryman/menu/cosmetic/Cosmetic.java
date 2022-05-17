package cc.ikew.deliveryman.menu.cosmetic;

import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.utils.ChatUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Cosmetic {

    private final String configSection;
    public String id;
    public List<String> commands;

    public Cosmetic(String configSection) {
        FileConfiguration config = ConfigManager.items;
        this.configSection = configSection;
        id = configSection.replace("cosmetics.", "");
        commands = config.contains(configSection + ".commands") ? config.getStringList(configSection + ".commands") : new ArrayList<String>();
    }

    public ItemStack getByPlayer(Player p){
        FileConfiguration config = ConfigManager.items;
        ItemStack is = new ItemStack(Material.valueOf(config.getString(configSection + ".material").toUpperCase()));
        is.setAmount(config.contains(configSection + ".amount") ? config.getInt(configSection + ".amount") : 1);
        ItemMeta meta = is.getItemMeta();
        if(config.contains(configSection + ".glint") && config.getBoolean(configSection + ".glint")){
            meta.addEnchant(Enchantment.LURE, 1, false);
        }
        meta.setDisplayName(ChatUtils.translate(config.getString(configSection + ".name"), p));
        meta.setLore(ChatUtils.translateAll(p, config.getStringList(configSection + ".lore")));
        int modelData = (config.contains(configSection + ".custom-model-data") ? config.getInt(configSection + ".custom-model-data") : -1);
        if(modelData != -1) meta.setCustomModelData(modelData);
        is.setItemMeta(meta);
        NBTItem nbti = new NBTItem(is);
        nbti.setString(CosmeticsHandler.cosmeticKey, id);
        return nbti.getItem();
    }
}
