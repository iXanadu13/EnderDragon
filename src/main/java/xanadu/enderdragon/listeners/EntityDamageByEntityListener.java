package xanadu.enderdragon.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import xanadu.enderdragon.events.DragonDamageByPlayerEvent;
import xanadu.enderdragon.utils.MyDragon;

import static xanadu.enderdragon.EnderDragon.pm;
import static xanadu.enderdragon.manager.DragonManager.getSpecialKey;
import static xanadu.enderdragon.manager.DragonManager.mp;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonAttack(EntityDamageByEntityEvent e){
        Entity victim = e.getEntity();
        Entity attack = e.getDamager();
        if(!(attack instanceof EnderDragon dragon)) return;
        String unique_name = getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = mp.get(unique_name);
        if(myDragon == null) return;
        e.setDamage(Math.max(0.1, e.getDamage() + myDragon.attack_damage_modify));
        if(victim instanceof Player player){
            for(PotionEffect effect : myDragon.attack_potion_effect){
                player.addPotionEffect(effect);
            }
            if(!myDragon.suck_blood_enable) return;
            double suck = e.getFinalDamage() * myDragon.suck_blood_rate + myDragon.suck_blood_base_amount;
            dragon.setHealth(Math.min(dragon.getHealth()+suck,dragon.getMaxHealth()));
        }
        else{
            if(!myDragon.suck_blood_enable) return;
            if(myDragon.suck_blood_only_player) return;
            double suck = e.getFinalDamage() * myDragon.suck_blood_rate + myDragon.suck_blood_base_amount;
            dragon.setHealth(Math.min(dragon.getHealth()+suck,dragon.getMaxHealth()));
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ExtraAttackToDragon(EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getDamage() <= 0.0) return;
        Entity victim = e.getEntity();
        Entity entity = e.getDamager();
        if(victim instanceof EnderDragon dragon){
            if(entity instanceof TNTPrimed tnt){
                if(!(tnt.getSource() instanceof Player damager)) return;
                pm.callEvent(new DragonDamageByPlayerEvent(damager,dragon,e.getCause(),e.getFinalDamage()));
            }
        }
    }
}
