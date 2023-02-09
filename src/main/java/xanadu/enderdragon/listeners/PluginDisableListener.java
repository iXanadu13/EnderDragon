package xanadu.enderdragon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import static xanadu.enderdragon.EnderDragon.*;

public class PluginDisableListener implements Listener {
    @EventHandler
    public void OnPluginDisable(PluginDisableEvent e){
        if(e.getPlugin().equals(plugin)){
            disableAll();
        }
    }
}
