package pers.xanadu.enderdragon.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pers.xanadu.enderdragon.manager.GlowManager;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e){
        GlowManager.setScoreBoard(e.getPlayer());

    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e){
        DragonExplosionHurtListener.clearUID(e.getPlayer().getUniqueId());
    }
}
