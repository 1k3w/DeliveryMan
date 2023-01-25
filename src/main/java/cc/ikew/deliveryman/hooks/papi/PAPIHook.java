package cc.ikew.deliveryman.hooks.papi;

import cc.ikew.deliveryman.Deliveryman;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPIHook extends PlaceholderExpansion {
    private Deliveryman plugin; // This instance is assigned in canRegister()

    @Override
    public String getAuthor() {
        return "IKEW (same person)";
    }

    @Override
    public String getIdentifier() {
        return "deliveryman";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getRequiredPlugin() {
        return "Deliveryman";
    }

    @Override
    public boolean canRegister() {
        return (plugin = (Deliveryman) Bukkit.getPluginManager().getPlugin(getRequiredPlugin())) != null;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equals("available")) return (PAPIDataHandler.getRewardsRemaining(player) != 0 ?
                plugin.getConfig().getString("placeholderAPI.rewards-text").replace("{rewards}", PAPIDataHandler.getRewardsRemaining(player) + "") :
                plugin.getConfig().getString("placeholderAPI.no-rewards-text"));
        return "";
    }
}
