package pers.xanadu.enderdragon.listener;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.event.DragonRespawnEvent;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.util.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.*;
import static pers.xanadu.enderdragon.manager.DragonManager.*;
import static pers.xanadu.enderdragon.manager.GlowManager.*;

public class DragonSpawnListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonSpawn(final CreatureSpawnEvent e){
        if(!(e.getEntity() instanceof EnderDragon)) return;
        if(Config.blacklist_worlds.contains(e.getEntity().getWorld().getName())) return;
        if(Config.blacklist_spawn_reason.contains(e.getSpawnReason().name())) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        MyDragon myDragon = null;
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT){
            myDragon = getDesignatedDragon(dragon.getWorld());
        }
        if(myDragon == null) myDragon = judge();
        if(myDragon == null) {
            Lang.warn("special_dragon_jude_mode setting error!");
            return;
        }
        setSpecialKey(dragon, myDragon.unique_name);
        DamageManager.data.put(dragon.getUniqueId(),new ConcurrentHashMap<>());
        int times = data.getInt("times");
        Lang.runCommands(myDragon.spawn_cmd);
        for(String str : myDragon.spawn_broadcast_msg){
            Lang.broadcastMSG(str.replaceAll("%times%",String.valueOf(times)));
        }
        dragon.setCustomName(myDragon.display_name);
        setAttribute(dragon, Attribute.GENERIC_MAX_HEALTH, myDragon.max_health);
        dragon.setHealth(myDragon.spawn_health);
        dragon.setMaximumNoDamageTicks(myDragon.no_damage_tick);

        //modifyAttribute(dragon, Attribute.GENERIC_MOVEMENT_SPEED, myDragon.move_speed_modify);//

        modifyAttribute(dragon, Attribute.GENERIC_ARMOR, myDragon.armor_modify);

        modifyAttribute(dragon, Attribute.GENERIC_ARMOR_TOUGHNESS, myDragon.armor_toughness_modify);
        String color = myDragon.glow_color.toUpperCase();
        if(color.equals("NONE")) dragon.setGlowing(false);
        else setGlowingColor(dragon,getGlowColor(color));
        String bossBar_color = myDragon.bossbar_color.toUpperCase();
        String bossBar_style = myDragon.bossbar_style.toUpperCase();

        if(Version.mcMainVersion >= 14){
            BossBar bossBar = dragon.getBossBar();
            if(bossBar != null){
                bossBar.setColor(BarColor.valueOf(bossBar_color));
                bossBar.setStyle(BarStyle.valueOf(bossBar_style));
            }
        }
        else if(Version.mcMainVersion >= 12){
            getInstance().getBossBarManager().setBossBar(dragon.getWorld(),myDragon.display_name,bossBar_color,bossBar_style);
        }
        Bukkit.getPluginManager().callEvent(new DragonRespawnEvent(dragon,e.getSpawnReason(),myDragon));
    }

}
