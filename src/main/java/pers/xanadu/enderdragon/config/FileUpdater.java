package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class FileUpdater {
    public static void update() throws IOException {

        FileConfiguration config_old = plugin.getConfig();
        if("2.2.0".equals(config_old.getString("version"))){
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
            config_new.set("auto_respawn",config_old.getConfigurationSection("auto_respawn"));

            config_new.set("respawn_cd.enable",config_old.getBoolean("respawn_cd.enable"));
            config_new.set("crystal_invulnerable",config_old.getBoolean("crystal_invulnerable"));
            config_new.set("resist_player_respawn",config_old.getBoolean("resist_player_respawn"));
            config_new.set("resist_dragon_breath_gather",config_old.getBoolean("resist_dragon_breath_gather"));
            config_new.set("main_gui",config_old.getString("main_gui"));
            config_new.set("blacklist_worlds",config_old.getStringList("blacklist_worlds"));
            config_new.set("item_format.reward",config_old.getString("item_format.reward"));
            config_new.set("hook_plugins.MythicLib",config_old.getBoolean("hook_plugins.MythicLib"));
            config_new.set("expansion.groovy",config_old.getBoolean("expansion.groovy"));
            config_new.set("debug",config_old.getBoolean("debug"));
            config_new.set("advanced_setting.world_env_fix",config_old.getBoolean("advanced_setting.world_env_fix"));
            config_new.set("advanced_setting.save_respawn_status",config_old.getBoolean("advanced_setting.save_respawn_status"));
            config_new.set("save_bossbar",config_old.getBoolean("save_bossbar"));
            config_new.set("backslash_split.reward",config_old.getBoolean("backslash_split.reward"));
            config_new.save(config_new_F);
        }
        else Lang.error("The version of config.yml is not supported!");

        Lang.info("New config files are generated in plugins/EnderDragon/new.");
        Lang.warn("Attention: Please confirm the accuracy before using new config!");
    }
}
