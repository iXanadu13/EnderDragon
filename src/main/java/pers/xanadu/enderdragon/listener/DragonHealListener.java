package pers.xanadu.enderdragon.listener;

import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.WorldManager;
import pers.xanadu.enderdragon.metadata.MyDragon;

import static pers.xanadu.enderdragon.manager.DragonManager.getSpecialKey;

public class DragonHealListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void OnDragonHeal(final EntityRegainHealthEvent e){
        if(!(e.getEntity() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        if(!WorldManager.enable_worlds.contains(dragon.getWorld().getName())) return;
        if(!e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL)) return;
        String unique_name = getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = DragonManager.get_dragon_config(unique_name);
        if(myDragon == null) return;
        e.setAmount(myDragon.crystal_heal_speed / 2d);
    }

}
