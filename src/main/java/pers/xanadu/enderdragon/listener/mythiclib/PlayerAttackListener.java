package pers.xanadu.enderdragon.listener.mythiclib;

import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pers.xanadu.enderdragon.event.DragonDamageByPlayerEvent;
import pers.xanadu.enderdragon.manager.WorldManager;

import static pers.xanadu.enderdragon.EnderDragon.pm;

public class PlayerAttackListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(final PlayerAttackEvent e){
        if(e.isCancelled()) return;
        if(!(e.getEntity() instanceof org.bukkit.entity.EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        if(!WorldManager.enable_worlds.contains(dragon.getWorld().getName())) return;
        DamageMetadata damage = e.getDamage();
        double final_damage = damage.getDamage();
        if(final_damage > 0.0d){
            DragonDamageByPlayerEvent event = new DragonDamageByPlayerEvent(e.getAttacker().getPlayer(),dragon, e.toBukkit().getCause(), final_damage);
            pm.callEvent(event);
            if(event.isCancelled()) e.setCancelled(true);
        }
    }

}
