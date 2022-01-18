package nl.hyperminecraft.deliveryman.profile;

import nl.hyperminecraft.deliveryman.MySql.DataHandler;
import nl.hyperminecraft.deliveryman.config.ConfigManager;
import nl.hyperminecraft.deliveryman.reward.Reward;
import nl.hyperminecraft.deliveryman.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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

    public int getAvailableRewards(){

        return 0;
    }

    public void claimReward(Reward reward){
        ClaimableState state = getClaimableState(reward);
        if (state == ClaimableState.AVAILABLE){
            for (String s : reward.commands){
                if (s.startsWith("[c] ")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("[c] ", ""));
                if (s.startsWith("[m] ")) player.sendMessage(ChatUtils.translate( s.replace("[m] ", ""), player));
            }
            DataHandler.setRedeemed(player.getUniqueId(), reward.id, System.currentTimeMillis(), this);
            lastClaimed.put(reward.id, System.currentTimeMillis());
        }else if(state == ClaimableState.ALREADY_CLAIMED){
            String message = ConfigManager.messages.getString("already-claimed");
            player.sendMessage(ChatUtils.translate(message.replace("{claim_next_remain}", (reward.cooldown <= -1) ? "Never" : getReadableTimeRemaining(reward)), player));
        }else{
            player.sendMessage(ChatUtils.translate(ConfigManager.messages.getString("not-allowed-to-claim"), player));
        }
    }

    public ClaimableState getClaimableState(Reward reward){
        boolean hasPerms = true;
        for (String perm : reward.requiredPermissions) if (!player.hasPermission(perm)) hasPerms = false;
        if (lastClaimed.containsKey(reward.id)){
            if (System.currentTimeMillis() - lastClaimed.get(reward.id) <= reward.cooldown || reward.cooldown == -1) return ClaimableState.ALREADY_CLAIMED;
            return ClaimableState.AVAILABLE;
        }
        return hasPerms ? ClaimableState.AVAILABLE : ClaimableState.UNAVAILABLE;
    }

    public String getReadableTimeRemaining(Reward reward){
        if (reward.cooldown <= -1) return "nooit";
        long remainingTime = (lastClaimed.get(reward.id) + reward.cooldown - System.currentTimeMillis() ) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(remainingTime);
        long hour = TimeUnit.SECONDS.toHours(remainingTime) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(remainingTime) - (TimeUnit.SECONDS.toHours(remainingTime)* 60);
        long second = TimeUnit.SECONDS.toSeconds(remainingTime) - (TimeUnit.SECONDS.toMinutes(remainingTime)* 60);
        return String.format( day + " &ddagen &5" + hour + " &duur &5" + minute + " &dmin &5" + second + " &dsec");
    }

    public boolean hasClaimed(String reward) {
        return lastClaimed.containsKey(reward);
    }
}
