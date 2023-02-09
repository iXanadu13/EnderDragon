package xanadu.enderdragon.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xanadu.enderdragon.events.DragonDamageByPlayerEvent;

import static xanadu.enderdragon.EnderDragon.pm;

public class DragonBaseHurtListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void BaseAttackToDragon(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getDamage() <= 0.0) return;
        Entity victim = e.getEntity();
        Entity entity = e.getDamager();
        if(victim instanceof EnderDragon dragon){
            if(entity instanceof Player player){
                pm.callEvent(new DragonDamageByPlayerEvent(player,dragon,e.getCause(),e.getFinalDamage()));
            }
            else if(entity instanceof Projectile projectile){
                if(!(projectile.getShooter() instanceof Player damager)) return;
                pm.callEvent(new DragonDamageByPlayerEvent(damager,dragon,e.getCause(),e.getFinalDamage()));
            }
        }
    }
}
