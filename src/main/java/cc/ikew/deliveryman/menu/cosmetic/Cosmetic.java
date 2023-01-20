package cc.ikew.deliveryman.menu.cosmetic;

import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.configgable.Configgable;
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

import static cc.ikew.deliveryman.config.ConfigManager.config;

public class Cosmetic {

    private final String configSection;
    public String id;
    public List<String> commands;

    private Configgable<String> material;
    private Configgable<Integer> amount;
    private Configgable<Boolean> glint;
    private Configgable<String> displayName;
    private Configgable<List<String>> lore;
    private Configgable<Integer> cmd;

    public Cosmetic(String configSection) {
        FileConfiguration config = ConfigManager.items;
        this.configSection = configSection;
        id = configSection.replace("cosmetics.", ""); // cosmetics.ID_HERE.properties
        commands = config.contains(configSection + ".commands") ? config.getStringList(configSection + ".commands") : new ArrayList<String>();

        material = new Configgable<>(configSection + ".material", ConfigManager.items, ConfigManager.itemsFile);
        amount = new Configgable<>(configSection + ".amount", ConfigManager.items, ConfigManager.itemsFile);
        glint = new Configgable<>(configSection + ".glint", ConfigManager.items, ConfigManager.itemsFile);
        displayName = new Configgable<>(configSection + ".name", ConfigManager.items, ConfigManager.itemsFile);
        lore = new Configgable<>(configSection + ".lore", ConfigManager.items, ConfigManager.itemsFile);
        cmd = new Configgable<>(configSection + ".custom-model-data", ConfigManager.items, ConfigManager.itemsFile);
    }

    public ItemStack getByPlayer(Player p){

        ItemStack is = new ItemStack(Material.valueOf(material.getOrDefault("stone").toUpperCase()));
        is.setAmount(amount.getOrDefault(1));
        ItemMeta meta = is.getItemMeta();
        if(glint.getOrDefault(false)){
            meta.addEnchant(Enchantment.LURE, 1, false);
        }
        meta.setDisplayName(ChatUtils.translate(displayName.getOrDefault(""), p));
        meta.setLore(ChatUtils.translateAll(p, lore.getOrDefault(new ArrayList<>())));
        int modelData = cmd.getOrDefault(-1);
        if(modelData != -1) meta.setCustomModelData(modelData);
        is.setItemMeta(meta);
        NBTItem nbti = new NBTItem(is);
        nbti.setString(CosmeticsHandler.cosmeticKey, id);
        return nbti.getItem();
    }
}
