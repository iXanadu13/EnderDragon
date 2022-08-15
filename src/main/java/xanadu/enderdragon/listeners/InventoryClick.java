package xanadu.enderdragon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static xanadu.enderdragon.EnderDragon.plugin;

public class InventoryClick implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void OnInventoryClick(InventoryClickEvent e){
        String title = plugin.getConfig().getString("special-dragon.drop-gui-title");
        if(!e.getView().getTitle().contains(title)){return;}
        e.setCancelled(true);
    }
}
