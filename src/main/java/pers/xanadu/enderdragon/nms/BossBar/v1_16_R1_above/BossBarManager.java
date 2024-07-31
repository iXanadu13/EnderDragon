package pers.xanadu.enderdragon.nms.BossBar.v1_16_R1_above;

import org.bukkit.World;
import org.bukkit.boss.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.nms.BossBar.IBossBarManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class BossBarManager implements IBossBarManager {
    public void saveBossBarData(List<World> worlds){
        File file = new File(plugin.getDataFolder(),"world_data.yml");
        YamlConfiguration yml = new YamlConfiguration();
        char split = yml.options().pathSeparator();
        worlds.forEach(world -> {
            DragonBattle battle = world.getEnderDragonBattle();
            if(battle == null) return;
            BossBar bossBar = battle.getBossBar();
            String path = world.getName() + split;
            yml.set(path+"title",bossBar.getTitle());
            yml.set(path+"color",bossBar.getColor().name());
            yml.set(path+"style",bossBar.getStyle().name());
            yml.set(path+"create_fog",bossBar.hasFlag(BarFlag.CREATE_FOG));
            yml.set(path+"darken_sky",bossBar.hasFlag(BarFlag.DARKEN_SKY));
            yml.set(path+"play_boss_music",bossBar.hasFlag(BarFlag.PLAY_BOSS_MUSIC));
        });
        try{
            yml.save(file);
            Lang.info("BossBar data has been saved!");
        }catch (IOException e){
            Lang.error("Failed to save world_data!");
        }
    }
    public void loadBossBarData(List<World> worlds){
        File file = new File(plugin.getDataFolder(),"world_data.yml");
        if(!file.exists()) return;
        Lang.info("Enabling BossBar fix...");
        YamlConfiguration yml = new YamlConfiguration();
        char split = yml.options().pathSeparator();
        try{
            yml.load(file);
        }catch (InvalidConfigurationException | IOException e) {
            Lang.error("Failed to load world_data!");
            return;
        }
        worlds.forEach(world -> {
            DragonBattle battle = world.getEnderDragonBattle();
            if(battle == null) return;
            BossBar bossBar = battle.getBossBar();
            String path = world.getName() + split;
            bossBar.setTitle(yml.getString(path+"title"));
            bossBar.setColor(BarColor.valueOf(yml.getString(path+"color")));
            bossBar.setStyle(BarStyle.valueOf(yml.getString(path+"style")));
            if(yml.getBoolean(path+"create_fog",true)) bossBar.addFlag(BarFlag.CREATE_FOG);
            else bossBar.removeFlag(BarFlag.CREATE_FOG);
            if(yml.getBoolean(path+"darken_sky",true)) bossBar.addFlag(BarFlag.DARKEN_SKY);
            else bossBar.removeFlag(BarFlag.DARKEN_SKY);
            if(yml.getBoolean(path+"play_boss_music",true)) bossBar.addFlag(BarFlag.PLAY_BOSS_MUSIC);
            else bossBar.removeFlag(BarFlag.PLAY_BOSS_MUSIC);
        });
    }
    public void setBossBar(World world, final MyDragon myDragon){
    }
}