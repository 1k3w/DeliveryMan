package nl.hyperminecraft.deliveryman.listener;

import nl.hyperminecraft.deliveryman.MySql.DataHandler;
import nl.hyperminecraft.deliveryman.profile.DeliveryPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

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
