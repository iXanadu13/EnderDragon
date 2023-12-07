package pers.xanadu.enderdragon.nms.BossBar.v1_13_R2;

import net.minecraft.server.v1_13_R2.BossBattle;
import net.minecraft.server.v1_13_R2.BossBattleServer;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.nms.BossBar.I_BossBarManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class BossBarManager implements I_BossBarManager {
    private Field BossBattleServer = null;
    public void saveBossBarData(List<World> worlds){
        File file = new File(plugin.getDataFolder(),"world_data.yml");
        YamlConfiguration yml = new YamlConfiguration();
        worlds.forEach(world -> {
            try{
                Object edb = DragonManager.getEnderDragonBattle(world);
                assert edb != null;
                if(BossBattleServer == null) BossBattleServer = edb.getClass().getDeclaredField("bossBattle");
                BossBattleServer.setAccessible(true);
                BossBattleServer bbs = (BossBattleServer) BossBattleServer.get(edb);
                String path = world.getName() + yml.options().pathSeparator();
                yml.set(path+"title",bbs.title.getText());
                yml.set(path+"color",bbs.color.name());
                yml.set(path+"style",bbs.style.name());
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
                String path = world.getName() + yml.options().pathSeparator();
                bbs.title = CraftChatMessage.fromString(yml.getString(path+"title"), true)[0];
                bbs.color = BossBattle.BarColor.valueOf(yml.getString(path+"color"));
                bbs.style = BossBattle.BarStyle.valueOf(yml.getString(path+"style"));
            }catch (ReflectiveOperationException e){
                e.printStackTrace();
            }
        });
    }
    public void setBossBar(World world,String title,String color,String style){
        BossBattleServer bbs = new BossBattleServer(
                CraftChatMessage.fromString(title, true)[0],
                convertColor(color),
                convertStyle(style)
        );
        bbs.setCreateFog(true);
        bbs.setDarkenSky(true);
        bbs.setPlayMusic(true);
        try{
            Object edb = DragonManager.getEnderDragonBattle(world);
            assert edb != null;
            if(BossBattleServer == null) BossBattleServer = edb.getClass().getField("bossBattle");
            BossBattleServer.setAccessible(true);
            BossBattleServer.set(edb,bbs);
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
    private BossBattle.BarColor convertColor(String color) {
        return BossBattle.BarColor.valueOf(color);
    }
    private BossBattle.BarStyle convertStyle(String style) {
        switch (style) {
            case "SOLID":
            default: return net.minecraft.server.v1_13_R2.BossBattle.BarStyle.PROGRESS;
            case "SEGMENTED_6": return net.minecraft.server.v1_13_R2.BossBattle.BarStyle.NOTCHED_6;
            case "SEGMENTED_10": return net.minecraft.server.v1_13_R2.BossBattle.BarStyle.NOTCHED_10;
            case "SEGMENTED_12": return net.minecraft.server.v1_13_R2.BossBattle.BarStyle.NOTCHED_12;
            case "SEGMENTED_20": return net.minecraft.server.v1_13_R2.BossBattle.BarStyle.NOTCHED_20;
        }
    }
}
