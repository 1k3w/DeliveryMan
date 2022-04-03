package cc.ikew.deliveryman.listener;

import cc.ikew.deliveryman.MySql.DataHandler;
import cc.ikew.deliveryman.profile.DeliveryPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RegisterListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        DataHandler.addDeliveryPlayer(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        DeliveryPlayer.players.remove(e.getPlayer());
    }

}
