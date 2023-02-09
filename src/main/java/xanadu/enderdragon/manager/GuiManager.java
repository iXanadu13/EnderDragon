package xanadu.enderdragon.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xanadu.enderdragon.gui.GUIWrapper;
import xanadu.enderdragon.config.Lang;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import static xanadu.enderdragon.EnderDragon.plugin;
import static xanadu.enderdragon.config.Lang.info;
import static xanadu.enderdragon.config.Lang.sendFeedback;

public class GuiManager {
    private static HashMap<String, GUIWrapper> f = new HashMap<>();
    public static void loadGui(){
        new BukkitRunnable(){
            @Override
            public void run(){
                File folder = new File(plugin.getDataFolder(),"gui");
                if(!folder.exists()) return;
                File[] files = folder.listFiles();
                if(files == null) return;
                for(File file : files){
                    if(!file.getName().endsWith(".yml")) continue;
                    info(Lang.plugin_read_file + file.getName());
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
        }.runTaskAsynchronously(plugin);
    }
    public static void disable(){
        f.clear();
    }
    public static void openGui(Player player, String name) {
        if (!f.containsKey(name)) {
            sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(name),name).current());
    }
    public static void openGui(Player player,String name,String key){
        if (!f.containsKey(name)) {
            sendFeedback(player,Lang.gui_not_found);
            return;
        }
        player.openInventory(new GUIWrapper(f.get(name),key).current());
    }
}
