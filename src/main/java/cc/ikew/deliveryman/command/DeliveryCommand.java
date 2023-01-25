package cc.ikew.deliveryman.command;

import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.menu.RewardMenuHandler;
import cc.ikew.deliveryman.menu.cosmetic.CosmeticsHandler;
import cc.ikew.deliveryman.reward.RewardManager;
import cc.ikew.deliveryman.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeliveryCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatUtils.translate("&cYou are not a player!"));
            return true;
        }
        Player p = (Player) commandSender;
        if (args.length == 0){
            if (!p.hasPermission("deliveryman.menu.default")){
                p.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("not-allowed").replace("{permission}", "deliveryman.menu.default"), p));
                return true;
            }
            RewardMenuHandler.getInstance().openMenu(p, "default");
        }else if (args[0].equalsIgnoreCase("reload")){
            if(!p.hasPermission("deliveryman.reloaded")){
                p.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("not-allowed").replace("{permission}", "deliveryman.reload"), p));
                return true;
            }
            ConfigManager.reloadConfigs();
            RewardMenuHandler.getInstance().load(ConfigManager.menus);
            RewardManager.getInstance().load(ConfigManager.items);
            CosmeticsHandler.getInstance().load();
            p.sendMessage(ChatColor.GREEN + "Reloaded all config files!");
        }else{
            if (!p.hasPermission("deliveryman.menu" + args[0].toLowerCase())){
                p.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("not-allowed").replace("{permission}", "deliveryman.menu." + args[0].toLowerCase()), p));
                return true;
            }
            RewardMenuHandler.getInstance().openMenu(p, args[0]);
        }
        return true;
    }
}
