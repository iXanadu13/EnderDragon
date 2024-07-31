package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class FileUpdater {
    public static void update() throws IOException {
        boolean updated = false;
        File folder = new File(plugin.getDataFolder(),"setting");
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(files != null){
                for(File file : files){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String ver = config.getString("version");
                    if(!"2.4.0".equals(ver)) {
                        Lang.error("The version of setting/"+file.getName()+" is not supported!");
                        continue;
                    }
                    updated = true;
                    String name = file.getName();
                    Config.copyFile("setting/"+name,"new/setting/"+name,true);
                }
                File new_folder = new File(plugin.getDataFolder(),"new/setting/");
                files = new_folder.listFiles();
                if(files != null){
                    for(File file : files){
                        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                        fc.set("version","2.5.4");
                        fc.set("bossbar.create_fog",fc.getBoolean("bossbar.create_frog"));
                        fc.set("bossbar.create_frog",null);
                        fc.save(file);
                    }
                }
            }
        }
        if (updated){
            Lang.info("New config files are generated in plugins/EnderDragon/new.");
            Lang.warn("Attention: Please confirm the accuracy before using new config!");
        }
        else {
            Lang.warn("Nothing to update!");
        }
    }
}
