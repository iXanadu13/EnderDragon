package xanadu.enderdragon.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void OnInventoryClick(InventoryClickEvent e){
        if(!e.getView().getTitle().contains("§b特殊龙掉落物§r")){return;}
        e.setCancelled(true);
    }
}
