package xanadu.enderdragon.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.EnderDragon;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.gui.Reward;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static xanadu.enderdragon.EnderDragon.*;
import static xanadu.enderdragon.config.Config.saveResource;
import static xanadu.enderdragon.config.Lang.error;
import static xanadu.enderdragon.config.Lang.info;
import static xanadu.enderdragon.manager.ItemManager.readAsItem;

public class FileUpdater {
    public static void update() throws IOException {
        FileConfiguration config_old = plugin.getConfig();
        FileConfiguration data_old = EnderDragon.data;
        info("New setting files will be generated in plugins/EnderDragon/new.");
        if("1.8.3".equals(config_old.getString("version"))){
            File base_F = new File(plugin.getDataFolder(),"setting/default.yml");
            File default_F = new File(plugin.getDataFolder(),"new/setting/default.yml");
            FileConfiguration default_ = YamlConfiguration.loadConfiguration(base_F);
            default_.set("display_name",config_old.getString("normal-dragon.name","Ender Dragon"));
            default_.set("crystal_heal_speed",config_old.getDouble("normal-dragon.crystal-heal",2.0d));
            if(config_old.getBoolean("command.enable")){
                default_.set("death_cmd",config_old.getStringList("command.normal-dragon"));
            }
            default_.save(default_F);
            File base2_F = new File(plugin.getDataFolder(),"setting/special.yml");
            File special_F = new File(plugin.getDataFolder(),"new/setting/special.yml");
            FileConfiguration special = YamlConfiguration.loadConfiguration(base2_F);
            String pre = "special-dragon.";
            special.set("display_name",config_old.getString(pre+"name"));
            special.set("spawn_chance",config_old.getString(pre+"chance"));
            special.set("max_health",config_old.getDouble(pre+"max-health"));
            special.set("spawn_health",config_old.getDouble(pre+"spawn-health"));
            special.set("attack_potion_effect",config_old.getStringList(pre+"attack-effect"));
            special.set("exp_drop",config_old.getInt(pre+"exp-drop"));
            special.set("dragon_egg_spawn.chance",config_old.getDouble(pre+"dragon-egg-spawn.chance"));
            special.set("dragon_egg_spawn.delay",config_old.getInt(pre+"dragon-egg-spawn.delay"));
            special.set("dragon_egg_spawn.x",config_old.getInt(pre+"dragon-egg-spawn.x"));
            special.set("dragon_egg_spawn.y",config_old.getInt(pre+"dragon-egg-spawn.y"));
            special.set("dragon_egg_spawn.z",config_old.getInt(pre+"dragon-egg-spawn.z"));
            String color = config_old.getString(pre+"glow-color");
            if("disable".equalsIgnoreCase(color)) color = "none";
            special.set("glow_color",color);
            special.set("crystal_heal_speed",config_old.getDouble(pre+"crystal-heal"));
            special.set("suck_blood.enable",config_old.getBoolean(pre+"suck-blood.enable"));
            special.set("suck_blood.rate",config_old.getDouble(pre+"suck-blood.rate"));
            special.set("suck_blood.base_amount",config_old.getDouble(pre+"suck-blood.base-suck-blood"));
            special.set("suck_blood.only_player",config_old.getBoolean(pre+"suck-blood.only-player"));
            if(config_old.getBoolean("command.enable")){
                special.set("death_cmd",config_old.getStringList(pre+"special-dragon"));
            }
            special.save(special_F);
        }
        else Lang.error("Your config.yml version is not supported!");
        if("1.8.4".equals(data_old.getString("version"))){
            saveResource("data.yml","new/data.yml",true);
            File data_new_F = new File(plugin.getDataFolder(),"new/data.yml");
            FileConfiguration data_new = YamlConfiguration.loadConfiguration(data_new_F);
            data_new.set("version","2.0.0");
            data_new.set("times",data_old.getInt("times"));
            List<String> items_old = data_old.getStringList("items");
            try{
                File base2_F = new File(plugin.getDataFolder(),"setting/special.yml");
                FileConfiguration special = YamlConfiguration.loadConfiguration(base2_F);
                String special_key = special.getString("unique_name");
                List<String> list = new ArrayList<>();
                for(int i=0;i+1<items_old.size();i+=2){
                    YamlConfiguration yml = new YamlConfiguration();
                    yml.loadFromString(items_old.get(i));
                    ItemStack item = readAsItem(yml,"item");
                    String str = items_old.get(i+1);
                    double d0 = Double.parseDouble(str);
                    Reward reward = new Reward(item,new Chance(d0,str));
                    list.add(reward.toString());
                }
                if(special_key == null){
                    error("\"unique_name\" in setting/special.yml is missing.Update data.yml failed!");
                    return;
                }
                data_new.set(special_key,list);
                data_new.save(data_new_F);
            }catch (InvalidConfigurationException | NumberFormatException | IOException e){
                error("The format of data.yml is invalid!");
            }
        }
        else Lang.error("Your data.yml version is not supported!");
        if("1.8.3".equals(config_old.getString("version"))){
            saveResource("config.yml","new/config.yml",true);
            File config_new_F = new File(plugin.getDataFolder(),"new/config.yml");
            FileConfiguration config_new = YamlConfiguration.loadConfiguration(config_new_F);
            config_new.set("lang",config_old.getString("lang"));
            config_new.save(config_new_F);
        }
        String lang_name = config_old.getString("lang","English") + ".yml";
        FileConfiguration lang_old = lang;
        if("1.8.3".equals(lang_old.getString("version"))){
            saveResource("lang/"+lang_name,"new/lang/"+lang_name,true);
            File lang_new_F = new File(plugin.getDataFolder(),"new/lang/"+lang_name);
            FileConfiguration lang_new = YamlConfiguration.loadConfiguration(lang_new_F);
            lang_new.set("plugin.prefix",lang_old.getString("prefix"));
            lang_new.set("command.no_permission",lang_old.getString("NoCommandPermission"));
            lang_new.set("command.reload_config",lang_old.getString("configReloaded"));
            lang_new.set("command.only_player",lang_old.getString("PlayerCommand"));
            lang_new.set("command.drop_item.clear",lang_old.getString("ClearDropItemConfig"));
            lang_new.set("dragon.player_inv_full",lang_old.getString("player-inv-full"));
            lang_new.set("dragon.no_killer",lang_old.getString("nobody-kill"));
            lang_new.save(lang_new_F);
        }
        else error("Your "+lang_name+" version is not supported!");

    }
}
