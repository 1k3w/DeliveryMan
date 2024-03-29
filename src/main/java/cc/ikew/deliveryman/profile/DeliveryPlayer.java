package cc.ikew.deliveryman.profile;

import cc.ikew.deliveryman.Deliveryman;
import cc.ikew.deliveryman.MySql.DataHandler;
import cc.ikew.deliveryman.MySql.MySql;
import cc.ikew.deliveryman.config.ConfigManager;
import cc.ikew.deliveryman.hooks.vault.VaultHook;
import cc.ikew.deliveryman.reward.Reward;
import cc.ikew.deliveryman.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DeliveryPlayer {
    public static HashMap<Player, DeliveryPlayer> players = new HashMap();

    public static DeliveryPlayer getDeliveryPlayer(Player player){
        return players.get(player);
    }

    public static void addDeliveryPlayer(DeliveryPlayer player){
        players.put(player.getPlayer(), player);
    }

    private Player player;
    private HashMap<String, Long> lastClaimed;

    public DeliveryPlayer(Player player, HashMap<String, Long> lastClaimed) {
        this.player = player;
        this.lastClaimed = lastClaimed;
    }

    public Player getPlayer() {
        return player;
    }

    public void claimReward(Reward reward){
        try{
            if (MySql.conn.isClosed()) return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ClaimableState state = getClaimableState(reward);
        if (state == ClaimableState.AVAILABLE){
            //System.out.println("claimable");
            for (String s : reward.commands.get()){
                if (s.startsWith("[c] ") || s.startsWith("[c]")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("[c]", "")
                .replace("{player}", player.getName()).replace("{name}", player.getName()));
                if (s.startsWith("[m] ") || s.startsWith("[m]")) player.sendMessage(ChatUtils.translate( s.replace("[m]", ""), player));
            }
            DataHandler.setRedeemed(player.getUniqueId(), reward.id, System.currentTimeMillis(), this);
            lastClaimed.put(reward.id, System.currentTimeMillis());
            if(reward.claimSound != null) player.playSound(player.getLocation(), reward.claimSound.get(), 1f, 1f);

        }else if(state == ClaimableState.ALREADY_CLAIMED){
            //System.out.println("already claimed");
            String message = ConfigManager.messages.getString("already-claimed");
            player.sendMessage(ChatUtils.translate(message.replace("{claim_next_remain}", (reward.cooldown.get() <= -1) ?
                    ConfigManager.messages.getString("never-claimable-text") : getReadableTimeRemaining(reward)), player));
            if(reward.unavailableSound != null) player.playSound(player.getLocation(), reward.unavailableSound.get(), 1f, 1f);
        }else{
            //System.out.println("not claimable");
            player.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("not-allowed-to-claim"), player));
            if (reward.needConditionSound != null) player.playSound(player.getLocation(), reward.needConditionSound.get(), 1f, 1f);
        }
    }

    public ClaimableState getClaimableState(Reward reward){
        boolean hasPerms = true;
        for (String req : reward.conditions.get()) {
            if (req.startsWith("[perm]")){
                req = req.replace("[perm] ", "").replace("[perm]", "");
                if (!player.hasPermission(req) && !player.isOp()) {
                    hasPerms = false;
                    break;
                }
                continue;
            }
            if (req.startsWith("[money]")){
                req = req.replace("[money] ", "").replace("[money]", "");
                try{
                    double moneyRequired = Double.parseDouble(req);
                    if (!VaultHook.hasMoney(moneyRequired, player)){
                        hasPerms = false;
                        break;
                    }
                }catch(NumberFormatException fex){
                    Deliveryman.instance.getLogger().log(Level.SEVERE, "Check your items.yml, fix it, and then use /dm reload");
                    hasPerms = false;
                }
            }

        }
        if (lastClaimed.containsKey(reward.id)){
            if (System.currentTimeMillis() - lastClaimed.get(reward.id) <= reward.cooldown.get() || reward.cooldown.get() == -1) return ClaimableState.ALREADY_CLAIMED;
            return ClaimableState.AVAILABLE;
        }
        return hasPerms ? ClaimableState.AVAILABLE : ClaimableState.UNAVAILABLE;
    }

    public String getReadableTimeRemaining(Reward reward){
        if (reward.cooldown.get() <= -1) return ConfigManager.messages.getString("never-claimable-text");
        long remainingTime = (lastClaimed.get(reward.id) + reward.cooldown.get() - System.currentTimeMillis() ) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(remainingTime);
        long hour = TimeUnit.SECONDS.toHours(remainingTime) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(remainingTime) - (TimeUnit.SECONDS.toHours(remainingTime)* 60);
        long second = TimeUnit.SECONDS.toSeconds(remainingTime) - (TimeUnit.SECONDS.toMinutes(remainingTime)* 60);
        return ConfigManager.messages.getString("date-time-format")
                .replace("dd", day + "")
                .replace("hh", hour + "")
                .replace("mm", minute + "")
                .replace("ss", second + "");
    }

    public boolean hasClaimed(String reward) {
        return lastClaimed.containsKey(reward);
    }
}
