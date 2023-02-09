package xanadu.enderdragon;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xanadu.enderdragon.commands.MainCommand;
import xanadu.enderdragon.commands.TabCompleter;
import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.gui.GUIHolder;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.listeners.*;
import xanadu.enderdragon.listeners.mmoitems.MMOPlayerAttackListener;
import xanadu.enderdragon.listeners.mythiclib.PlayerAttackListener;
import xanadu.enderdragon.manager.DragonManager;
import xanadu.enderdragon.manager.GuiManager;
import xanadu.enderdragon.manager.TaskManager;
import xanadu.enderdragon.metrics.Metrics;
import xanadu.enderdragon.task.RespawnDragonRunnable;
import xanadu.enderdragon.utils.Version;

import java.io.*;
import java.util.Objects;

import static xanadu.enderdragon.config.Lang.*;
import static xanadu.enderdragon.manager.GuiManager.loadGui;
import static xanadu.enderdragon.utils.Updater.checkUpdate;

public final class EnderDragon extends JavaPlugin {

    private static EnderDragon instance;
    public static Plugin plugin;
    public static Server server;
    public static PluginManager pm;
    public static File dataF;
    public static File langF;
    public static FileConfiguration data;
    public static FileConfiguration lang;
    public static int mcMainVersion;
    public static int mcPatchVersion;
    private DragonManager dragonManager;
    private static boolean finish;
    @Override
    public void onEnable() {
        finish = false;
        info("Enabling plugin...");
        info("Author: Xanadu13");
        plugin = this;
        instance = this;
        dragonManager = new DragonManager();
        server = plugin.getServer();
        pm = Bukkit.getPluginManager();
        mcMainVersion = getMinecraftVersion();
        registerEvents();
        registerCommands();
        Metrics metrics = new Metrics(this,14850);
        reloadAll();
        Version.init();
        checkUpdate();
        if(!Lang.version.equals("2.0.0")){
            warn(Lang.plugin_wrong_file_version.replace("{file_name}",Config.lang + ".yml"));
        }
        if(!Config.version.equals("2.0.0")){
            warn(Lang.plugin_wrong_file_version.replace("{file_name}","config.yml"));
        }
        if(!"2.0.0".equals(data.getString("version"))){
            warn(Lang.plugin_wrong_file_version.replace("{file_name}","data.yml"));
        }
        finish = true;
    }
    public static void reloadAll(){
        EnderDragon.getInstance().loadFiles();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!TaskManager.getCurrentTimeWithSpecialFormat().endsWith("0")) return;
                this.cancel();
                RespawnDragonRunnable.reload();
            }
        };
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!finish) return;
                this.cancel();
                DragonManager.reload();
                loadGui();
                if(Config.auto_respawn_enable){
                    TaskManager.reload();
                    runnable.runTaskTimer(plugin,0,1);
                }
            }
        }.runTaskTimer(plugin, 1, 5);
    }
    public static void disableAll(){
        closeAllInventory();
        Bukkit.getScheduler().cancelTasks(plugin);
        DragonManager.disable();
        GuiManager.disable();
        instance.unregisterCommands();
        HandlerList.unregisterAll(plugin);
        server.getScheduler().cancelTasks(plugin);
        server.getServicesManager().unregisterAll(plugin);
        warn(Lang.plugin_disable);
        System.gc();
    }
    private void registerEvents(){
        pm.registerEvents(new CreatureSpawnListener(),this);
        pm.registerEvents(new DragonDamageByPlayerListener(),this);
        pm.registerEvents(new DragonDeathListener(),this);
        pm.registerEvents(new DragonHealListener(),this);
        pm.registerEvents(new EntityDamageByEntityListener(),this);
        pm.registerEvents(new InventoryListener(),this);
        pm.registerEvents(new PluginDisableListener(),this);
        if(pm.getPlugin("MythicLib") != null){
            info("Hooking to MythicLib...");
            pm.registerEvents(new PlayerAttackListener(),this);
        }
        else{
            pm.registerEvents(new DragonBaseHurtListener(),this);
        }
        if(pm.getPlugin("MMOItems") != null){
            info("Hooking to MMOItems...");
            pm.registerEvents(new MMOPlayerAttackListener(),this);
        }
    }
    private void registerCommands(){
        Objects.requireNonNull(getCommand("enderdragon")).setExecutor(new MainCommand());
        Objects.requireNonNull(getCommand("enderdragon")).setTabCompleter(new TabCompleter());
    }
    private void unregisterCommands(){
        Objects.requireNonNull(getCommand("enderdragon")).setExecutor(null);
        Objects.requireNonNull(getCommand("enderdragon")).setTabCompleter(null);
    }
    private void loadFiles(){
        saveDefaultConfig();
        reloadConfig();
        Config.reload(getConfig());
        if (!new File(getDataFolder(), "data.yml").exists()) {
            this.saveResource("data.yml",false);
        }
        dataF = new File(getDataFolder(),"data.yml");
        data = YamlConfiguration.loadConfiguration(dataF);
        if(Config.lang.equals("")) {
            error("Key \"lang\" in config is missing, please check your config.yml.");
            Config.lang = "English";
        }
        String langPath = "lang/" + Config.lang + ".yml";
        if (!new File(getDataFolder(), langPath).exists()) {
            this.saveResource(langPath, false);
        }
        langF = new File(getDataFolder(), langPath);
        lang = YamlConfiguration.loadConfiguration(langF);
        info("Language: §6" + Config.lang);
        Lang.reload(lang);
        if (!new File(plugin.getDataFolder(), "gui/view.yml").exists()) {
            plugin.saveResource("gui/view.yml",false);
        }

    }
    public static EnderDragon getInstance() {return instance;}
    private int getMinecraftVersion() {
        String[] version = getServer().getBukkitVersion().replace('-', '.').split("\\.");
        try {
            mcPatchVersion = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {}
        return Integer.parseInt(version[1]);
    }
    public static void closeAllInventory() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Inventory inventory = player.getOpenInventory().getTopInventory();
                if (!(inventory.getHolder() instanceof GUIHolder)) continue;
                player.closeInventory();
            }
            catch (Exception ignored) {}
        }
    }

    public DragonManager getDragonManager(){
        return dragonManager;
    }

}


