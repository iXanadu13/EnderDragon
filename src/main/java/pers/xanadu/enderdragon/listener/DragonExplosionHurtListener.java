package pers.xanadu.enderdragon.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import pers.xanadu.enderdragon.event.DragonDamageByPlayerEvent;
import pers.xanadu.enderdragon.event.PlayerExplodeDragonEvent;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.pm;
import static pers.xanadu.enderdragon.manager.WorldManager.getExplosionDragon;

public class DragonExplosionHurtListener implements Listener {

    private static final ConcurrentHashMap<UUID,PlayerExplodeDragonEvent> mp = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDragonDamageByTNT(final EntityDamageByEntityEvent e){
        if(e.isCancelled()) return;
        if(e.getDamage() <= 0.0) return;
        Entity victim = e.getEntity();
        Entity entity = e.getDamager();
        if(victim instanceof EnderDragon){
            EnderDragon dragon = (EnderDragon) victim;
            if(entity instanceof TNTPrimed){
                TNTPrimed tnt = (TNTPrimed) entity;
                if(!(tnt.getSource() instanceof Player)) return;
                Player damager = (Player) tnt.getSource();
                pers.xanadu.enderdragon.EnderDragon.pm.callEvent(new DragonDamageByPlayerEvent(damager,dragon,e.getCause(),e.getFinalDamage()));
            }
        }
    }

    /**
     * 用tnt隔着方块炸龙也能触发
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDragonDamageByExplode(final EntityDamageByBlockEvent e){
        long time = pers.xanadu.enderdragon.EnderDragon.getInstance().getWorldDataManager().getGameTime(e.getEntity().getWorld());
        EntityDamageEvent.DamageCause cause = e.getCause();
        if(e.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;
        Entity entity = e.getEntity();
        if(entity instanceof EnderDragon){
            //Bukkit.broadcastMessage(e.getFinalDamage()+"awa");
            EnderDragon dragon = (EnderDragon) entity;
            for(UUID uuid : mp.keySet()){
                PlayerExplodeDragonEvent ped = mp.get(uuid);
                if(ped == null) continue;
                if(ped.getTime() != time) continue;
                if(ped.getEnderDragon().getUniqueId() != dragon.getUniqueId()) continue;
                //mp.remove(uuid);
                pm.callEvent(new DragonDamageByPlayerEvent(ped.getPlayer(),dragon,cause,e.getFinalDamage()));
                //Bukkit.broadcastMessage(ped._loc.toString());
                //Bukkit.broadcastMessage("final: "+e.getFinalDamage());
                break;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void OnPlayerClickBed(final PlayerBedEnterEvent e){
        Player p = e.getPlayer();
        if(p.getWorld().getEnvironment() != World.Environment.THE_END) return;
        long time = pers.xanadu.enderdragon.EnderDragon.getInstance().getWorldDataManager().getGameTime(e.getPlayer().getWorld());
        Location bed_loc = e.getBed().getLocation();
        Collection<EnderDragon> entities = getExplosionDragon(5f,bed_loc);
        for(EnderDragon dragon : entities){
            UUID uuid = p.getUniqueId();
            mp.put(uuid,new PlayerExplodeDragonEvent(time,p,dragon,bed_loc));
            break;
        }
    }


    public static void clearUID(final UUID uuid){
        mp.remove(uuid);
    }
    public static void addUID(final UUID uuid,final PlayerExplodeDragonEvent ped){
        mp.put(uuid,ped);
    }

}
