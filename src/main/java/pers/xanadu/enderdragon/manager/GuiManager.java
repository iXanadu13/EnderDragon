package pers.xanadu.enderdragon.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.gui.GUIWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class GuiManager {
    private static HashMap<String, GUIWrapper> f = new HashMap<>();
    public static void loadGui(){
        File folder = new File(plugin.getDataFolder(),"gui");
        if(!folder.exists()) return;
        File[] files = folder.listFiles();
        if(files == null) return;
        for(File file : files){
            if(!file.getName().endsWith(".yml")) continue;
            Lang.info(Lang.plugin_read_file + file.getName());
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            Iterator it = fileConfiguration.getKeys(false).iterator();
            while (it.hasNext()){
                String name = (String) it.next();
                ConfigurationSection section = fileConfiguration.getConfigurationSection(name);
                GUIWrapper guiWrapper = new GUIWrapper(section);
                f.put(name, guiWrapper);
            }
        }
    }
    public static void disable(){
        f.clear();
    }
    public static void openGui(Player player, String name, boolean editor) {
        if (!f.containsKey(name)) {
            Lang.sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(name),name,editor).current());
    }
    public static void openGui(Player player,String style,String key, boolean editor){
        if (!f.containsKey(style)) {
            Lang.sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(style),key,editor).current());
    }

    @Deprecated
    public static void openGui(Player player, String name) {
        if (!f.containsKey(name)) {
            Lang.sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(name),name,false).current());
    }
    @Deprecated
    public static void openGui(Player player,String style,String key){
        if (!f.containsKey(style)) {
            Lang.sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(style),key,false).current());
    }
}
