package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.util.MyDragon;

import java.io.*;
import java.util.List;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class FileUpdater {
    public static void update() throws IOException {
        FileConfiguration config_old = plugin.getConfig();
        if("2.0.4".equals(config_old.getString("version"))){
            Config.saveResource("config.yml","new/config.yml",true);
            File config_new_F = new File(plugin.getDataFolder(),"new/config.yml");
            FileConfiguration config_new = YamlConfiguration.loadConfiguration(config_new_F);
            config_new.set("lang",config_old.getString("lang"));
            config_new.set("damage_visible_mode",config_old.getString("damage_visible_mode"));
            String judge_mode = config_old.getString("special_dragon_jude_mode");
            if("edge".equals(judge_mode)) judge_mode = "weight";//"edge" is deprecated
            config_new.set("special_dragon_jude_mode",judge_mode);
            config_new.set("dragon_setting_file",config_old.getStringList("dragon_setting_file"));
            config_new.set("auto_respawn.enable",config_old.getBoolean("auto_respawn.enable"));
            config_new.set("auto_respawn.world_the_end_name",config_old.getString("auto_respawn.world_the_end_name"));
            config_new.set("auto_respawn.respawn_time",config_old.getString("auto_respawn.respawn_time"));
            config_new.set("auto_respawn.invulnerable",config_old.getBoolean("auto_respawn.invulnerable"));
            config_new.set("resist_player_respawn",config_old.getBoolean("resist_player_respawn"));
            config_new.set("resist_dragon_breath_gather",config_old.getBoolean("resist_dragon_breath_gather"));
            config_new.set("main_gui",config_old.getString("main_gui"));
            config_new.save(config_new_F);
        }
        else Lang.error("The version of config.yml is not supported!");
        File folder = new File(plugin.getDataFolder(),"setting");
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(files != null){
                for(File file : files){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String ver = config.getString("version");
                    if(!"2.0.1".equals(ver)) {
                        Lang.error("The version of setting/"+file.getName()+" is not supported!");
                        continue;
                    }
                    String name = file.getName();
                    Config.copyFile("setting/"+name,"new/setting/"+name,true);
                }
                File new_folder = new File(plugin.getDataFolder(),"new/setting/");
                files = new_folder.listFiles();
                if(files != null){
                    for(File file : files){
                        FileOutputStream out = new FileOutputStream(file,true);
                        out.write(("\n\n" +
                                "reward_dist:\n" +
                                "  # enable: [all,drop,killer,pack,rank,termwise]\n" +
                                "  # all: Give to all participants in dragon slaying.\n" +
                                "  # drop: Dropped item, players can grab it casually.\n" +
                                "  # killer: Only give to the final killer.\n" +
                                "  # pack: Pack all items that trigger drop and distribute them to players weighted based on their damage percentage.\n" +
                                "  # rank: Strictly based on player damage ranking, give the top few players with the highest damage.\n" +
                                "  # termwise: Assign the items that trigger the drop ONE BY ONE to the player based on the weighted proportion of damage.\n" +
                                "  type: killer\n" +
                                "  drop:\n" +
                                "    # Whether the dropped item glows.\n" +
                                "    # Refer to the previous 'glow_color' for configuration method\n" +
                                "    glow: green\n" +
                                "  pack:\n" +
                                "    # the number of player(s) can be selected at most\n" +
                                "    max_num: 1\n" +
                                "  rank:\n" +
                                "    # Top few can receive rewards\n" +
                                "    max_num: 1\n" +
                                "\n"
                        ).getBytes());
                        out.close();
                        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                        fc.set("version","2.1.0");
                        fc.set("move_speed_modify",null);
                        fc.save(file);
                    }
                }
            }
        }
        String lang_name = config_old.getString("lang","English") + ".yml";
        FileConfiguration lang_old = lang;
        if("2.0.0".equals(lang_old.getString("version"))){
            Config.saveResource("lang/"+lang_name,"new/lang/"+lang_name,true);
            File lang_new_F = new File(plugin.getDataFolder(),"new/lang/"+lang_name);
            FileConfiguration lang_new = YamlConfiguration.loadConfiguration(lang_new_F);
            lang_old.getKeys(true).forEach(key->{
                Object obj = lang_old.get(key);
                if(obj instanceof String){
                    lang_new.set(key,obj);
                }
            });
            lang_new.set("version","2.1.0");
            lang_new.save(lang_new_F);
        }
        else Lang.error("The version of "+lang_name+" is not supported!");
        FileConfiguration data_old = EnderDragon.data;
        if("2.0.0".equals(data_old.getString("version"))){
            File new_folder = new File(plugin.getDataFolder(),"new/reward");
            for (MyDragon myDragon : DragonManager.dragons) {
                String key = myDragon.unique_name;
                List<String> list = data.getStringList(key);
                File new_data = new File(new_folder,key+".yml");
                FileConfiguration fc = YamlConfiguration.loadConfiguration(new_data);
                fc.set("version","2.1.0");
                fc.set("list",list);
                fc.save(new_data);
                data_old.set(key,null);
            }
            data_old.set("version","2.1.0");
            data_old.save(new File(plugin.getDataFolder(),"new/data.yml"));
        }
        else Lang.error("The version of data.yml is not supported!");
        Lang.info("New config files are generated in plugins/EnderDragon/new.");
        Lang.error("Attention: Please confirm the accuracy before using new config!");
    }
}
