package xanadu.enderdragon.listeners.mythiclib;

import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xanadu.enderdragon.events.DragonDamageByPlayerEvent;

import static xanadu.enderdragon.EnderDragon.pm;

public class PlayerAttackListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerAttack(PlayerAttackEvent e){
        if(e.isCancelled()) return;
        if(!(e.getEntity() instanceof org.bukkit.entity.EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        DamageMetadata damage = e.getDamage();
        double weapon = damage.getDamage(DamageType.WEAPON) + damage.getDamage(DamageType.UNARMED);
        if(weapon > 0.0d) {
            DragonDamageByPlayerEvent event = new DragonDamageByPlayerEvent(e.getAttacker().getPlayer(),dragon, e.toBukkit().getCause(), weapon);
            pm.callEvent(event);
            return;
        }
        double skill = damage.getDamage(DamageType.SKILL);
        if(skill >= 0.0d){
            DragonDamageByPlayerEvent event = new DragonDamageByPlayerEvent(e.getAttacker().getPlayer(),dragon, e.toBukkit().getCause(), skill);
            pm.callEvent(event);
            return;
        }
    }

}
