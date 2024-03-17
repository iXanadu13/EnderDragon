package pers.xanadu.enderdragon.nms.BossBar.v1_14_R1;

import net.minecraft.server.v1_14_R1.BossBattle;
import net.minecraft.server.v1_14_R1.BossBattleServer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.nms.BossBar.IBossBarManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class BossBarManager implements IBossBarManager {
    private Field BossBattleServer = null;
    public void saveBossBarData(List<World> worlds){
        File file = new File(plugin.getDataFolder(),"world_data.yml");
        YamlConfiguration yml = new YamlConfiguration();
        char split = yml.options().pathSeparator();
        worlds.forEach(world -> {
            try{
                Object edb = DragonManager.getEnderDragonBattle(world);
                assert edb != null;
                if(BossBattleServer == null) BossBattleServer = edb.getClass().getDeclaredField("bossBattle");
                BossBattleServer.setAccessible(true);
                BossBattleServer bbs = (BossBattleServer) BossBattleServer.get(edb);
                String path = world.getName() + split;
                yml.set(path+"title",bbs.title.getText());
                yml.set(path+"color",bbs.color.name());
                yml.set(path+"style",bbs.style.name());
                yml.set(path+"create_frog",bbs.isCreateFog());
                yml.set(path+"darken_sky",bbs.isDarkenSky());
                yml.set(path+"play_boss_music",bbs.isPlayMusic());
            }catch (ReflectiveOperationException e){
                e.printStackTrace();
            }
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
            try{
                Object edb = DragonManager.getEnderDragonBattle(world);
                assert edb != null;
                if(BossBattleServer == null) BossBattleServer = edb.getClass().getDeclaredField("bossBattle");
                BossBattleServer.setAccessible(true);
                BossBattleServer bbs = (BossBattleServer) BossBattleServer.get(edb);
                String path = world.getName() + split;
                bbs.title = CraftChatMessage.fromString(yml.getString(path+"title"), true)[0];
                bbs.color = BossBattle.BarColor.valueOf(yml.getString(path+"color"));
                bbs.style = BossBattle.BarStyle.valueOf(yml.getString(path+"style"));
                bbs.setCreateFog(yml.getBoolean(path+"create_frog",true));
                bbs.setDarkenSky(yml.getBoolean(path+"darken_sky",true));
                bbs.setPlayMusic(yml.getBoolean(path+"play_boss_music",true));
            }catch (ReflectiveOperationException e){
                e.printStackTrace();
            }
        });
    }
    public void setBossBar(World world, final MyDragon myDragon){
    }
}