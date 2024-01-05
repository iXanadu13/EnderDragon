package pers.xanadu.enderdragon.nms.BossBar.v1_16_R1_above;

import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.nms.BossBar.I_BossBarManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class BossBarManager implements I_BossBarManager {
    public void saveBossBarData(List<World> worlds){
        File file = new File(plugin.getDataFolder(),"world_data.yml");
        YamlConfiguration yml = new YamlConfiguration();
        worlds.forEach(world -> {
            DragonBattle battle = world.getEnderDragonBattle();
            if(battle == null) return;
            BossBar bossBar = battle.getBossBar();
            String path = world.getName() + yml.options().pathSeparator();
            yml.set(path+"title",bossBar.getTitle());
            yml.set(path+"color",bossBar.getColor().name());
            yml.set(path+"style",bossBar.getStyle().name());
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
            String path = world.getName() + yml.options().pathSeparator();
            bossBar.setTitle(yml.getString(path+"title"));
            bossBar.setColor(BarColor.valueOf(yml.getString(path+"color")));
            bossBar.setStyle(BarStyle.valueOf(yml.getString(path+"style")));
        });
    }
    public void setBossBar(World world, final MyDragon myDragon){
    }
}