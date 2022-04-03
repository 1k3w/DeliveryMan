package cc.ikew.deliveryman.hooks.vault;

import cc.ikew.deliveryman.Deliveryman;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy econ = null;
    private static boolean enabled = false;

    public static void setupEconomy(Deliveryman plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
        enabled = econ != null;
    }

    public static boolean hasMoney(double money, Player player){
        if (!enabled) return false;
        return econ.has(player, money);
    }

    public static void takeMoney(double money, Player player){
        if (!enabled) return;
        econ.withdrawPlayer(player, money);
    }
}
