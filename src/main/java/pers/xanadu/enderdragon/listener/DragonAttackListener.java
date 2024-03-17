package pers.xanadu.enderdragon.listener;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.WorldManager;
import pers.xanadu.enderdragon.util.ExtraPotionEffect;
import pers.xanadu.enderdragon.metadata.MyDragon;

public class DragonAttackListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonAttack(final EntityDamageByEntityEvent e){
        Entity victim = e.getEntity();
        Entity attack = e.getDamager();
        if(!(attack instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) attack;
        if(!WorldManager.enable_worlds.contains(dragon.getWorld().getName())) return;
        String unique_name = DragonManager.getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = DragonManager.get_dragon_config(unique_name);
        if(myDragon == null) return;
        e.setDamage(Math.max(0.1, e.getDamage() + myDragon.attack_damage_modify));
        if(victim instanceof Player){
            Player player = (Player) victim;
            for(PotionEffect effect : myDragon.attack_potion_effect){
                player.addPotionEffect(effect);
            }
            for(ExtraPotionEffect effect : myDragon.attack_extra_effect){
                effect.apply(player);
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

}
