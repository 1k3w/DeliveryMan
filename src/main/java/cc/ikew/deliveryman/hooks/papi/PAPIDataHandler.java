package cc.ikew.deliveryman.hooks.papi;

import cc.ikew.deliveryman.profile.ClaimableState;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import cc.ikew.deliveryman.reward.Reward;
import cc.ikew.deliveryman.reward.RewardManager;
import org.bukkit.OfflinePlayer;

public class PAPIDataHandler {

    private static boolean enabled = false;

    public static void enable(){ enabled = true;}

    public static int getRewardsRemaining(OfflinePlayer player){
        if (player.getPlayer() == null) return 0;
        DeliveryPlayer dp = DeliveryPlayer.getDeliveryPlayer(player.getPlayer());

        int i = 0;
        for (Reward r : RewardManager.getInstance().rewards.values()){
            if (ClaimableState.AVAILABLE == dp.getClaimableState(r)) i++;
        }
        return i;
    }

    public static boolean hookEnabled(){
        return enabled;
    }
}
