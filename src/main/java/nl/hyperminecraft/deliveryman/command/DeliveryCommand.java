package nl.hyperminecraft.deliveryman.command;

import nl.hyperminecraft.deliveryman.config.ConfigManager;
import nl.hyperminecraft.deliveryman.menu.RewardMenuHandler;
import nl.hyperminecraft.deliveryman.reward.RewardManager;
import nl.hyperminecraft.deliveryman.utils.ChatUtils;
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
            RewardMenuHandler.getInstance().openMenu(p, "default");
        }else if (args[0].equalsIgnoreCase("reload")){
            ConfigManager.reloadConfigs();
            RewardMenuHandler.getInstance().load(ConfigManager.menus);
            RewardManager.getInstance().load(ConfigManager.rewards);
        }
        return true;
    }
}
