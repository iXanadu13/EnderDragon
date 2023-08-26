package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.xanadu.enderdragon.EnderDragon;

import java.io.*;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class FileUpdater {
    public static void update() throws IOException {
        String respawn_world_name = null;
        FileConfiguration config_old = plugin.getConfig();
        if("2.1.0".equals(config_old.getString("version"))){
            Config.saveResource("config.yml","new/config.yml",true);
            File config_new_F = new File(plugin.getDataFolder(),"new/config.yml");
            FileConfiguration config_new = YamlConfiguration.loadConfiguration(config_new_F);
            config_new.set("lang",config_old.getString("lang"));
            config_new.set("damage_visible_mode",config_old.getString("damage_visible_mode"));
            config_new.set("damage_statistics.limit",config_old.getInt("damage_statistics.limit"));
            String judge_mode = config_old.getString("special_dragon_jude_mode");
            if("edge".equals(judge_mode)) judge_mode = "weight";//"edge" is deprecated
            config_new.set("special_dragon_jude_mode",judge_mode);
            config_new.set("dragon_setting_file",config_old.getStringList("dragon_setting_file"));
            // auto_respawn
            config_new.set("auto_respawn.task1.enable",config_old.getBoolean("auto_respawn.enable"));
            respawn_world_name = config_old.getString("auto_respawn.world_the_end_name");
            config_new.set("auto_respawn.task1.world_name",respawn_world_name);
            config_new.set("auto_respawn.task1.respawn_time",config_old.getString("auto_respawn.respawn_time"));
            config_new.set("crystal_invulnerable",config_old.getBoolean("auto_respawn.invulnerable"));

            config_new.set("respawn_cd.enable",config_old.getBoolean("respawn_cd.enable"));
            config_new.set("resist_player_respawn",config_old.getBoolean("resist_player_respawn"));
            config_new.set("resist_dragon_breath_gather",config_old.getBoolean("resist_dragon_breath_gather"));
            config_new.set("main_gui",config_old.getString("main_gui"));
            config_new.set("blacklist_worlds",config_old.getStringList("blacklist_worlds"));
            //item_format.data -> item_format.reward
            config_new.set("item_format.reward",config_old.getString("item_format.data"));

            config_new.set("hook_plugins.MythicLib",config_old.getBoolean("hook_plugins.MythicLib"));
            config_new.set("debug",config_old.getBoolean("debug"));
            config_new.set("advanced_setting.world_env_fix",config_old.getBoolean("advanced_setting.world_env_fix"));
            config_new.set("advanced_setting.save_respawn_status",config_old.getBoolean("advanced_setting.save_respawn_status"));
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
                    if(!"2.1.0".equals(ver)) {
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
                                "# the least interval time between injuries (unit:tick, 1tick=0.05s)\n" +
                                "# only supports integer value\n" +
                                "no_damage_tick: 10\n" +
                                "\n" +
                                "attack:\n" +
                                "  #the damage modify when this dragon attacks player with body or limb (allow double and negative value)\n" +
                                "  damage_modify: 0\n" +
                                "\n" +
                                "  #the effect give to player when this ender dragon attacks player with body or limb\n" +
                                "  #the format is the same as effect_cloud.potion\n" +
                                "  potion_effect:\n" +
                                "    - ''\n" +
                                "    - ''\n" +
                                "    - ''\n" +
                                "\n" +
                                "  # the effect give to player when this ender dragon attacks player with body or limb\n" +
                                "  # format: (<type>: seconds)\n" +
                                "  extra_effect:\n" +
                                "    fire: 0\n" +
                                "    # \"freeze\" can only be used in server greater than or equal to version 1.17\n" +
                                "    freeze: 0\n" +
                                "\n"
                        ).getBytes());
                        out.close();
                        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                        fc.set("version","2.2.0");
                        fc.set("attack.damage_modify",fc.getDouble("attack_damage_modify"));
                        fc.set("attack_damage_modify",null);
                        fc.get("attack.potion_effect",fc.getStringList("attack_potion_effect"));
                        fc.set("attack_potion_effect",null);
                        fc.save(file);
                    }
                }
            }
        }
        String lang_name = config_old.getString("lang","English") + ".yml";
        FileConfiguration lang_old = lang;
        if("2.1.0".equals(lang_old.getString("version"))){
            Config.saveResource("lang/"+lang_name,"new/lang/"+lang_name,true);
            File lang_new_F = new File(plugin.getDataFolder(),"new/lang/"+lang_name);
            FileConfiguration lang_new = YamlConfiguration.loadConfiguration(lang_new_F);
            lang_old.getKeys(true).forEach(key->{
                Object obj = lang_old.get(key);
                lang_new.set(key,obj);
            });
            lang_new.set("version","2.2.0");
            lang_new.save(lang_new_F);
        }
        else Lang.error("The version of "+lang_name+" is not supported!");

        FileConfiguration data_old = EnderDragon.data;
        if("2.1.0".equals(data_old.getString("version"))){
            Config.copyFile("data.yml","new/data.yml",true);
            File new_data_file = new File(plugin.getDataFolder(),"new/data.yml");
            FileConfiguration new_data = YamlConfiguration.loadConfiguration(new_data_file);
            String next_time = new_data.getString("auto_respawn.next_respawn_time");
            new_data.set("version","2.2.0");
            if(next_time!=null && respawn_world_name!=null){
                new_data.set("auto_respawn.task1.world_name",respawn_world_name);
                new_data.set("auto_respawn.task1.next_respawn_time",next_time);
            }
            new_data.save(new_data_file);
        }
        else Lang.error("The version of data.yml is not supported!");
        Lang.info("New config files are generated in plugins/EnderDragon/new.");
        Lang.warn("Attention: Please confirm the accuracy before using new config!");
    }
}
