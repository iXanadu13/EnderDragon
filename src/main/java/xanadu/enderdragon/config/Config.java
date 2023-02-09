package xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import static xanadu.enderdragon.EnderDragon.plugin;
import static xanadu.enderdragon.config.Lang.error;

public class Config {
    public static String version;
    public static String lang;
    public static String damage_visible_mode;
    public static String special_dragon_jude_mode;
    public static boolean auto_respawn_enable;
    public static String auto_respawn_world_the_end_name;
    public static String auto_respawn_respawn_time;
    public static boolean auto_respawn_invulnerable;
    public static boolean resist_player_respawn;
    public static boolean resist_dragon_breath_gather;
    public static String main_gui;
    public static List<String> dragon_setting_file;
    public static void reload(FileConfiguration file){
        Field[] fields = Config.class.getFields();
        for(Field field : fields){
            Type type = field.getType();
            if(type.equals(java.util.List.class) || type.equals(java.lang.String.class)){
                try{
                    field.set(null,"");
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

}
