package xanadu.enderdragon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xanadu.enderdragon.commands.MainCommand;
import xanadu.enderdragon.commands.TabCompleter;
import xanadu.enderdragon.events.*;
import xanadu.enderdragon.lang.Message;
import xanadu.enderdragon.listeners.InventoryClick;
import xanadu.enderdragon.metrics.Metrics;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public final class EnderDragon extends JavaPlugin {

    public static String prefix = "";
    public static String language = "";
    public static String langPath = "";
    public static Plugin plugin;
    public static Server server;
    public static PluginManager pm;
    public static File dataF;
    public static File langF;
    public static FileConfiguration data;
    public static FileConfiguration lang;
    public static int mcMainVersion;
    public static int mcPatchVersion;

    @Override
    public void onEnable() {
        plugin = this;
        server = plugin.getServer();
        pm = Bukkit.getPluginManager();
        mcMainVersion = getMinecraftVersion();
        boolean configNotFound = false;
        boolean dataNotFound = false;
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
            configNotFound = true;
        }
        if (!new File(getDataFolder(), "data.yml").exists()) {
            this.saveResource("data.yml",false);
            dataNotFound = true;
        }
        language = plugin.getConfig().getString("lang","English");
        langPath = "lang/" + language + ".yml";
        if (!new File(getDataFolder(), langPath).exists()) {
            this.saveResource(langPath, false);
        }
        sendMessage("§a[EnderDragon] Language: §6" + language);
        dataF = new File(plugin.getDataFolder(),"data.yml");
        langF = new File(getDataFolder(), langPath);
        lang = YamlConfiguration.loadConfiguration(langF);
        data = YamlConfiguration.loadConfiguration(dataF);
        Message.loadLanguage();
        prefix = lang.getString("prefix","§7[§eEnderDragon§7]§r ");
        if(configNotFound){sendMessage(Message.configNotFound);}
        if(dataNotFound){sendMessage(Message.dataNotFound);}
        if(!getConfig().getString("version").equals("1.8.3") ){
            sendMessage(Message.configWrongVersion);
        }
        if(!data.getString("version").equals("1.8.4") ){
            sendMessage(Message.dataWrongVersion);
        }
        if(!lang.getString("version").equals("1.8.3") ){
            sendMessage(Message.langWrongVersion);
        }
        sendMessage(Message.loaded);
        sendMessage("§a[EnderDragon] Author: Xanadu13");
        pm.registerEvents(new CreatureHurt(),this);
        pm.registerEvents(new DragonAttack(),this);
        pm.registerEvents(new DragonDeath(),this);
        pm.registerEvents(new DragonHeal(),this);
        pm.registerEvents(new DragonSpawn(),this);
        pm.registerEvents(new InventoryClick(),this);
        getCommand("enderdragon").setExecutor(new MainCommand());
        getCommand("enderdragon").setTabCompleter(new TabCompleter());
        Metrics metrics = new Metrics(this,14850);
        sendMessage(Message.checkingUpdate);
        try {
            URLConnection conn = new URL("https://api.github.com/repos/iXanadu13/EnderDragon/releases/latest").openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(60000);
            InputStream is = conn.getInputStream();
            String line = new BufferedReader(new InputStreamReader(is)).readLine();
            is.close();
            String newVer = line.substring(line.indexOf("\"tag_name\"") + 13, line.indexOf("\"target_commitish\"") - 2);
            String localVer = plugin.getDescription().getVersion();
            if (!localVer.equals(newVer)) {
                sendMessage(Message.OutOfDate1.replace("{0}",localVer));
                sendMessage(Message.OutOfDate2.replace("{1}",newVer));
                sendMessage(Message.OutOfDate3);
            }
            else{
                sendMessage(Message.UpToDate.replace("{1}",newVer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable()
    {
        sendMessage(Message.onDisable);
    }


    public void sendMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage(msg);
    }


    public static String itemStackToString(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }
    public static ItemStack StringToItemStack(String str) {
        YamlConfiguration yml = new YamlConfiguration();
        ItemStack item;
        try {
            yml.loadFromString(str);
            item = yml.getItemStack("item");
        } catch (InvalidConfigurationException ex) {
            item = new ItemStack(Material.AIR, 1);
        }
        return item;
    }
    private int getMinecraftVersion() {
        String[] version = getServer().getBukkitVersion().replace('-', '.').split("\\.");
        try {
            mcPatchVersion = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {
        }
        return Integer.parseInt(version[1]);
    }
    public static boolean isSpecial(Entity e){
        if(mcMainVersion >= 11){
            return e.getScoreboardTags().contains("special");
        }
        else{
            String uuid = e.getUniqueId().toString();
            return data.getStringList("special").contains(uuid);
        }
    }

}


