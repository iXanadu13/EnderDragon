package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

import static pers.xanadu.enderdragon.EnderDragon.plugin;
import static pers.xanadu.enderdragon.config.Lang.error;

public class Config {
    public static String version;
    public static String lang;
    public static boolean debug;
    public static boolean advanced_setting_world_env_fix;
    public static boolean advanced_setting_save_respawn_status;
    public static boolean advanced_setting_glowing_fix;
    public static String damage_visible_mode;
    public static int damage_statistics_limit;
    public static String special_dragon_jude_mode;
    public static boolean auto_respawn_enable;
    public static String auto_respawn_world_the_end_name;
    public static String auto_respawn_respawn_time;
    public static boolean auto_respawn_invulnerable;
    public static boolean respawn_cd_enable;
    public static boolean resist_player_respawn;
    public static boolean resist_dragon_breath_gather;
    public static boolean hook_plugins_MythicLib;
    public static String main_gui;
    public static String item_format_data;
    public static List<String> dragon_setting_file;
    public static List<String> blacklist_worlds;
    public static void reload(FileConfiguration file){
        Field[] fields = Config.class.getFields();
        for(Field field : fields){
            Type type = field.getType();
            if(type.equals(java.lang.String.class)){
                try{
                    field.set(null,"");
                }catch (Exception ignored){}
            }
            else if(type.equals(java.util.List.class)){
                try{
                    field.set(null,new ArrayList());
                }catch (Exception ignored){}
            }
        }
        Iterator<String> it = file.getKeys(true).iterator();
        while (it.hasNext()){
            String str = it.next();
            try{
                if(file.isConfigurationSection(str)) continue;
                Config.class.getField(str.replace(".","_")).set(null, file.get(str));
            }catch (Exception e){
                error("Config loading error! Key: "+str);
            }
        }
        switch (special_dragon_jude_mode.toLowerCase()){
            case "pc" :
            case "weight" : break;
            default: {
                error("Wrong special_dragon_jude_mode type in config.yml! Only \"weight\" or \"pc\" is valid.");
            }
        }
    }

    public static void saveResource(String source,String to,boolean replace){
        if (source == null || source.equals("")) return;
        source = source.replace('\\', '/');
        InputStream in = plugin.getResource(source);
        if (in == null) return;
        File outFile = new File("plugins/EnderDragon", to);
        int lastIndex = to.lastIndexOf('/');
        File outDir = new File("plugins/EnderDragon", to.substring(0, Math.max(lastIndex, 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ignored) {}
    }
    public static void copyFile(String source, String dest,boolean replace) {
        if (source == null || source.equals("")) return;
        source = source.replace('\\', '/');
        File inFile = new File("plugins/EnderDragon", source);
        File outFile = new File("plugins/EnderDragon", dest);
        int lastIndex = dest.lastIndexOf('/');
        File outDir = new File("plugins/EnderDragon", dest.substring(0, Math.max(lastIndex, 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        try {
            if (!outFile.exists() || replace) {
                InputStream in = new FileInputStream(inFile);
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ignored) {
            Lang.error("Failed to copy file: "+inFile.getPath()+" -> "+outFile.getPath());
        }
    }

}
