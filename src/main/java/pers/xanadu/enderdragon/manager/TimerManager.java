package pers.xanadu.enderdragon.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.task.DragonRespawnTimer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class TimerManager {
    private static final HashMap<String, DragonRespawnTimer> mp = new HashMap<>();
    public static void enable(){
        File file = new File(plugin.getDataFolder(),"respawn_cd.yml");
        if(file.exists()){
            FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection section = fc.getConfigurationSection("respawn_cd");
            if(section == null) return;
            section.getKeys(false).forEach(name->{
                int set_time = section.getInt(name+".setTime");
                int rest_time = section.getInt(name+".remainTime");
                if(set_time>0){
                    boolean run = section.getBoolean(name+".isRunning");
                    DragonRespawnTimer timer = new DragonRespawnTimer(name,set_time,rest_time);
                    if(run) timer.run();
                    TimerManager.mp.put(name,timer);
                }
            });
        }
    }
    public static void save(){
        YamlConfiguration yml = new YamlConfiguration();
        char split = yml.options().pathSeparator();
        mp.forEach((k,v)->{
            ConfigurationSection section = yml.createSection("respawn_cd"+split+k);
            if(v.getRestTime()>0){
                //section.set("world_name",k);
                section.set("setTime",v.getSetTime());
                section.set("remainTime",v.getRestTime());
                section.set("isRunning",v.isRunning());
            }
        });
        try{
            yml.save(new File(plugin.getDataFolder(),"respawn_cd.yml"));
        }catch (IOException e){
            Lang.error("Failed to save respawn_cd.yml!");
        }
    }
    public static void startTimer(String world_name){
        DragonRespawnTimer timer = mp.get(world_name);
        if(timer != null){
            timer.run();
        }
    }
    public static void setTimer(String world_name, DragonRespawnTimer timer){
        DragonRespawnTimer timer_old = mp.get(world_name);
        if(timer_old != null) timer_old.del();
        mp.put(world_name,timer);
    }
    public static DragonRespawnTimer getTimer(String world_name){
        return mp.get(world_name);
    }
    public static void removeTimer(String world_name){
        DragonRespawnTimer timer_old = mp.get(world_name);
        if(timer_old != null) timer_old.del();
        mp.remove(world_name);
    }
    public static void removeAll(){
        mp.values().forEach(DragonRespawnTimer::del);
        mp.clear();
    }
}
