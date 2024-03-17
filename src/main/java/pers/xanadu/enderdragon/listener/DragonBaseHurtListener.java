package pers.xanadu.enderdragon.listener;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pers.xanadu.enderdragon.event.DragonDamageByPlayerEvent;
import pers.xanadu.enderdragon.manager.WorldManager;

import static pers.xanadu.enderdragon.EnderDragon.pm;

public class DragonBaseHurtListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void BaseAttackToDragon(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getDamage() <= 0.0) return;
        Entity victim = e.getEntity();
        Entity entity = e.getDamager();
        if(victim instanceof EnderDragon){
            if(!WorldManager.enable_worlds.contains(victim.getWorld().getName())) return;
            EnderDragon dragon = (EnderDragon) victim;
            if(entity instanceof Player){
                Player player = (Player) entity;
                DragonDamageByPlayerEvent event = new DragonDamageByPlayerEvent(player,dragon,e.getCause(),e.getFinalDamage());
                pm.callEvent(event);
                if(event.isCancelled()) e.setCancelled(true);
            }
            else if(entity instanceof Projectile){
                Projectile projectile = (Projectile) entity;
                if(!(projectile.getShooter() instanceof Player)) return;
                Player damager = (Player) projectile.getShooter();
                DragonDamageByPlayerEvent event = new DragonDamageByPlayerEvent(damager,dragon,e.getCause(),e.getFinalDamage());
                pm.callEvent(event);
                if(event.isCancelled()) e.setCancelled(true);
            }
        }
    }
}
