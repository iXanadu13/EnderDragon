package pers.xanadu.enderdragon.listener;

import com.ericdebouwer.petdragon.api.PetDragonAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.event.DragonRespawnPostEvent;
import pers.xanadu.enderdragon.hook.HookManager;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.manager.WorldManager;
import pers.xanadu.enderdragon.metadata.DragonInfo;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.*;
import static pers.xanadu.enderdragon.manager.DragonManager.*;
import static pers.xanadu.enderdragon.manager.GlowManager.*;

public class DragonSpawnListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void OnDragonSpawn(final CreatureSpawnEvent e){
        if(!(e.getEntity() instanceof EnderDragon)) return;
        //compatibility with plugin PetDragon
        if (HookManager.isPetDragonInstalled()){
            if (PetDragonAPI.getInstance().isPetDragon(e.getEntity())) return;
        }
        if(!WorldManager.enable_worlds.contains(e.getEntity().getWorld().getName())) return;
        if(Config.blacklist_spawn_reason.contains(e.getSpawnReason().name())) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        World world = dragon.getWorld();
        MyDragon myDragon = null;
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.DEFAULT){
            myDragon = getDesignatedDragon(world);
        }
        if(myDragon == null) myDragon = judge();
        if(myDragon == null) {
            Lang.warn("special_dragon_jude_mode setting error!");
            return;
        }
        DragonInfo info = new DragonInfo(dragon,myDragon.unique_name);
        Bukkit.getPluginManager().callEvent(new DragonRespawnPostEvent(dragon,e.getSpawnReason(),info));
        MyDragon update = getFromInfo(info);
        if(update == null) {
            Lang.error("Unknown unique_name in DragonInfo: "+info.unique_name);
        }
        else{
            myDragon = update;
        }
        {
            existing_dragon.put(dragon.getUniqueId(),new DragonInfo(dragon,myDragon.unique_name));
            DragonInfo old = main_dragon.get(world.getName());
            if(old == null || !old.getHandle().isValid()) main_dragon.put(world.getName(),new DragonInfo(dragon,myDragon.unique_name));
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

        if(Version.mcMainVersion >= 14){
            BossBar bossBar = dragon.getBossBar();
            if(bossBar != null){
                bossBar.setColor(BarColor.valueOf(myDragon.bossbar_color));
                bossBar.setStyle(BarStyle.valueOf(myDragon.bossbar_style));
                if(myDragon.bossbar_create_frog) bossBar.addFlag(BarFlag.CREATE_FOG);
                else bossBar.removeFlag(BarFlag.CREATE_FOG);
                if(myDragon.bossbar_darken_sky) bossBar.addFlag(BarFlag.DARKEN_SKY);
                else bossBar.removeFlag(BarFlag.DARKEN_SKY);
                if(myDragon.bossbar_play_boss_music) bossBar.addFlag(BarFlag.PLAY_BOSS_MUSIC);
                else bossBar.removeFlag(BarFlag.PLAY_BOSS_MUSIC);
            }
        }
        else if(Version.mcMainVersion >= 12){
            getInstance().getBossBarManager().setBossBar(world,myDragon);
        }
        if(Config.advanced_setting_save_bossbar){
            getInstance().getBossBarManager().saveBossBarData(Collections.singletonList(world));
        }
    }

}
