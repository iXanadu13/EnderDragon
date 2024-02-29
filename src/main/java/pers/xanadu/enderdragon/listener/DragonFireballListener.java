package pers.xanadu.enderdragon.listener;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffect;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.WorldManager;
import pers.xanadu.enderdragon.metadata.MyDragon;

import static pers.xanadu.enderdragon.manager.DragonManager.getSpecialKey;

public class DragonFireballListener implements Listener {

//    @EventHandler
//    public void OnDragonChangePhase(EnderDragonChangePhaseEvent e){
//        //Bukkit.broadcastMessage("old: "+e.getCurrentPhase());
//        //Bukkit.broadcastMessage("new: "+e.getNewPhase());
//    }
//    @EventHandler
//    public void OnDragonFireballLaunch(ProjectileLaunchEvent e){
//        Projectile projectile = e.getEntity();
//        if(!(projectile instanceof DragonFireball)) return;
//        Bukkit.broadcastMessage("DragonFireball launched!");
//        DragonFireball fireball = (DragonFireball) e.getEntity();
//        ProjectileSource source = projectile.getShooter();
//        if(source instanceof EnderDragon){
//            Bukkit.broadcastMessage("status set!");
//        }
//
//    }

    @EventHandler(priority = EventPriority.HIGH)
    public void OnEffectCloudSpawn(final EntitySpawnEvent e){
        if(!(e.getEntity() instanceof AreaEffectCloud)) return;
        AreaEffectCloud effectCloud = (AreaEffectCloud) e.getEntity();
        if(effectCloud.getSource() == null) return;
        if(!(effectCloud.getSource() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) effectCloud.getSource();
        if(!WorldManager.enable_worlds.contains(dragon.getWorld().getName())) return;
        String unique_name = getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = DragonManager.get_dragon_config(unique_name);
        if(myDragon == null) return;
        effectCloud.setRadius((float) myDragon.effect_cloud_original_radius);
        effectCloud.setRadiusPerTick((float) myDragon.effect_cloud_expand_speed/20f);
        effectCloud.setDuration(myDragon.effect_cloud_duration * 20);
        if(myDragon.effect_cloud_color_R != -1){
            effectCloud.setParticle(Particle.SPELL_MOB);
            effectCloud.setColor(Color.fromRGB(myDragon.effect_cloud_color_R,myDragon.effect_cloud_color_G,myDragon.effect_cloud_color_B));
        }
        effectCloud.clearCustomEffects();
        for(PotionEffect effect : myDragon.effect_cloud_potion){
            effectCloud.addCustomEffect(effect,true);
        }
    }
}
