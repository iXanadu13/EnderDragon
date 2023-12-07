package pers.xanadu.enderdragon;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.command.MainCommand;
import pers.xanadu.enderdragon.command.TabCompleter;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.gui.GUIHolder;
import pers.xanadu.enderdragon.hook.HookManager;
import pers.xanadu.enderdragon.listener.*;
import pers.xanadu.enderdragon.listener.mythiclib.PlayerAttackListener;
import pers.xanadu.enderdragon.manager.*;
import pers.xanadu.enderdragon.maven.DependencyManager;
import pers.xanadu.enderdragon.metrics.Metrics;
import pers.xanadu.enderdragon.nms.BossBar.I_BossBarManager;
import pers.xanadu.enderdragon.nms.NMSItem.I_NMSItemManager;
import pers.xanadu.enderdragon.nms.RespawnAnchor.I_RespawnAnchorManager;
import pers.xanadu.enderdragon.nms.WorldData.I_WorldDataManager;
import pers.xanadu.enderdragon.util.NMSUtil;
import pers.xanadu.enderdragon.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pers.xanadu.enderdragon.util.UpdateChecker.checkUpdate;

public final class EnderDragon extends JavaPlugin {
    private static EnderDragon instance;
    public static Plugin plugin;
    public static PluginManager pm;
    public static File dataF;
    public static File langF;
    public static FileConfiguration data;
    public static FileConfiguration lang;
    private WorldManager worldManager;
    private NMSUtil nmsManager;
    private I_BossBarManager i_bossBarManager;
    private I_NMSItemManager i_nmsItemManager;
    private I_WorldDataManager i_worldDataManager;
    private I_RespawnAnchorManager i_respawnAnchorManager;
    private static boolean finish = false;
    @Override
    public void onLoad(){
        plugin = this;
        instance = this;
        pm = Bukkit.getPluginManager();
        loadConfig();
        if(Config.expansion_groovy) DependencyManager.onEnable();
    }
    @Override
    public void onEnable() {
        Lang.info("Enabling plugin...");
        Lang.info("Author: Xanadu13");
        Version.init();
        worldManager = new WorldManager();
        nmsManager = new NMSUtil();
        nmsManager.init();
        i_bossBarManager = Version.getBossBarManager();
        i_nmsItemManager = Version.getNMSItemManager();
        i_worldDataManager = Version.getWorldDataManager();
        i_respawnAnchorManager = Version.getRespawnAnchorManager();
        if(Config.advanced_setting_world_env_fix) {
            getInstance().getWorldManager().fixWorldEnvironment();
            Lang.info(Lang.world_env_fix_enable);
        }
        if(Config.advanced_setting_save_bossbar){
            fixWorldBossBar();
        }
        TimerManager.enable();
        registerEvents();
        registerCommands();
        HookManager.init();
        reloadAll();
        if(Config.advanced_setting_save_respawn_status) setRespawnStatus();
//        if(Config.advanced_setting_glowing_fix) fixGlowing();
        checkUpdate();
        new Metrics(this,14850);
        boolean out_of_date = false;
        if(!Version.lang.equals(Lang.version)){
            out_of_date = true;
            Lang.warn(Lang.plugin_wrong_file_version.replace("{file_name}", Config.lang + ".yml"));
        }
        if(!Version.config.equals(Config.version)){
            out_of_date = true;
            Lang.warn(Lang.plugin_wrong_file_version.replace("{file_name}","config.yml"));
        }
        if(!Version.data.equals(data.getString("version"))){
            out_of_date = true;
            Lang.warn(Lang.plugin_wrong_file_version.replace("{file_name}","data.yml"));
        }
        if(out_of_date) Lang.warn("You can use /ed update to update the config!");
        finish = true;
    }
    public static void reloadAll(){
        WorldManager.reload();
        GlowManager.reload();
        EnderDragon.getInstance().loadFiles();
        if(DependencyManager.isGroovyLoaded()) {
            Config.saveDirectoryResource("expansion/groovy",false);
            GroovyManager.reload();
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!TaskManager.getCurrentTimeWithSpecialFormat().endsWith("0")) return;
                this.cancel();
                TaskManager.reload();
            }
        };
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!finish) return;
                this.cancel();
                DragonManager.reload();
                GuiManager.loadGui();
                runnable.runTaskTimer(plugin,0,1);
            }
        }.runTaskTimer(plugin, 1, 5);
    }
    @Override
    public void onDisable(){
        TimerManager.save();
        if(Config.advanced_setting_save_respawn_status){
            Bukkit.getWorlds().forEach(world -> {
                if(!Config.blacklist_worlds.contains(world.getName())){
                    char split = data.options().pathSeparator();
                    if(DragonManager.isRespawnRunning(world)){
                        data.set("respawn_fix"+split+world.getName(),true);
                    }
                    else data.set("respawn_fix"+split+world.getName(),false);
                }
            });
        }
        if(Config.advanced_setting_save_respawn_status || Config.advanced_setting_glowing_fix){
            try{
                data.save(dataF);
            }catch (IOException ex){
                Lang.error(Lang.plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
            }
        }
        if(Config.advanced_setting_save_bossbar){
            List<World> worlds = new ArrayList<>();
            Bukkit.getWorlds().forEach(world -> {
                if(world.getEnvironment()==World.Environment.THE_END&&!Config.blacklist_worlds.contains(world.getName())){
                    worlds.add(world);
                }
            });
            getInstance().getBossBarManager().saveBossBarData(worlds);
        }
    }
    public static void disableAll(){
        closeAllInventory();
        Bukkit.getScheduler().cancelTasks(plugin);
        DragonManager.disable();
        GuiManager.disable();
        instance.unregisterCommands();
        HandlerList.unregisterAll(plugin);
        plugin.getServer().getScheduler().cancelTasks(plugin);
        plugin.getServer().getServicesManager().unregisterAll(plugin);
        Lang.warn(Lang.plugin_disable);
        System.gc();
    }

    private void registerEvents(){
        pm.registerEvents(new DragonAttackListener(),this);
        pm.registerEvents(new DragonDamageByPlayerListener(),this);
        pm.registerEvents(new DragonDeathListener(),this);
        pm.registerEvents(new DragonExplosionHurtListener(),this);
        pm.registerEvents(new DragonFireballListener(),this);
        pm.registerEvents(new DragonHealListener(),this);
        pm.registerEvents(new DragonSpawnListener(),this);
        //pm.registerEvents(new EndGatewayListener(),this);
        pm.registerEvents(new InventoryListener(),this);
        pm.registerEvents(new PlayerListener(),this);
        pm.registerEvents(new PluginDisableListener(),this);
        if(Version.mcMainVersion>=16) pm.registerEvents(new RespawnAnchorExplodeListener(),this);
        if(Config.hook_plugins_MythicLib && pm.getPlugin("MythicLib") != null){
            Lang.info("Hooking to MythicLib...");
            pm.registerEvents(new PlayerAttackListener(),this);
        }
        else{
            pm.registerEvents(new DragonBaseHurtListener(),this);
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
    private void loadConfig(){
        saveDefaultConfig();
        reloadConfig();
        Config.reload(getConfig());
    }
    private void loadFiles(){
        if(finish) loadConfig();
        if (!new File(getDataFolder(), "data.yml").exists()) {
            this.saveResource("data.yml",false);
        }
        dataF = new File(getDataFolder(),"data.yml");
        data = YamlConfiguration.loadConfiguration(dataF);
        if(Config.lang.equals("")) {
            Lang.error("Key \"lang\" in config is missing, please check your config.yml.");
            Config.lang = "English";
        }
        String langPath = "lang/" + Config.lang + ".yml";
        if (!new File(getDataFolder(), langPath).exists()) {
            this.saveResource(langPath, false);
        }
        langF = new File(getDataFolder(), langPath);
        lang = YamlConfiguration.loadConfiguration(langF);
        Lang.info("Language: §6" + Config.lang);
        Lang.reload(lang);
        if (!new File(plugin.getDataFolder(), "gui/view.yml").exists()) {
            plugin.saveResource("gui/view.yml",false);
        }
//        if (!new File(plugin.getDataFolder(), "gui/cmd.yml").exists()) {
//            plugin.saveResource("gui/cmd.yml",false);
//        }
    }
    public static EnderDragon getInstance() {return instance;}

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
    private void fixWorldBossBar(){
        new BukkitRunnable(){
            @Override
            public void run(){
                List<World> worlds = new ArrayList<>();
                Bukkit.getWorlds().forEach(world -> {
                    if(world.getEnvironment()==World.Environment.THE_END&&!Config.blacklist_worlds.contains(world.getName())){
                        worlds.add(world);
                    }
                });
                getInstance().getBossBarManager().loadBossBarData(worlds);
            }
        }.runTaskAsynchronously(plugin);
    }
    private void fixGlowing(){
        new BukkitRunnable(){
            @Override
            public void run(){
                Lang.info("Glowing fix has been enabled!");
                ConfigurationSection section = data.getConfigurationSection("glowing_fix");
                if(section != null){
                    section.getKeys(false).forEach(key->{
                        ConfigurationSection world_section = section.getConfigurationSection(key);
                        if(world_section != null){
                            world_section.getKeys(false).forEach(uuid->{
                                String color = world_section.getString(uuid);
                                GlowManager.addUUID(uuid,color);
                            });
                        }
                    });
                }
            }
        }.runTaskLater(plugin,100L);
    }
    private void setRespawnStatus(){
        new BukkitRunnable(){
            @Override
            public void run(){
                ConfigurationSection section = data.getConfigurationSection("respawn_fix");
                if(section != null){
                    section.getKeys(false).forEach(key->{
                        if(section.getBoolean(key)){
                            DragonManager.refresh_respawn(Bukkit.getWorld(key));
                            Lang.info("DragonRespawn continues!");
                        }
                    });
                }
            }
        }.runTaskLater(plugin,100L);
    }

    public WorldManager getWorldManager(){
        return worldManager;
    }
    public NMSUtil getNMSManager(){
        return nmsManager;
    }
    public I_BossBarManager getBossBarManager(){
        return i_bossBarManager;
    }
    public I_NMSItemManager getNMSItemManager(){
        return i_nmsItemManager;
    }
    public I_WorldDataManager getWorldDataManager(){
        return i_worldDataManager;
    }
    public I_RespawnAnchorManager getRespawnAnchorManager(){
        return i_respawnAnchorManager;
    }

}


