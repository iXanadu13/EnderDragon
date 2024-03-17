package pers.xanadu.enderdragon.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.reward.Reward;
import pers.xanadu.enderdragon.reward.Chance;
import pers.xanadu.enderdragon.metadata.MyDragon;
import pers.xanadu.enderdragon.util.Version;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class RewardManager {

    public static void reload(){
        for(MyDragon dragon : DragonManager.dragons){
            dragon.datum.clear();
            File file = getRewardFile(dragon.unique_name);
            if(file == null) return;
            FileConfiguration data = loadConfiguration(file);
            String path = "list";
            List<String> list = data.getStringList(path);
            for(String str : list){
                YamlConfiguration yml = new YamlConfiguration();
                if(Config.advanced_setting_backslash_split_reward) yml.options().pathSeparator('\\');
                try{
                    yml.loadFromString(str);
                    Reward reward = ItemManager.readAsReward(yml);
                    dragon.datum.add(reward);
                }catch (InvalidConfigurationException e){
                    Lang.error(Lang.plugin_item_read_error + str);
                }
            }
        }
        if (ItemManager.isLegacy()){
            Lang.error("I'm sorry that data_type 'nbt' and 'advanced' probably will be disabled in 1.20.5+");
            Lang.error("It is recommended to migrate item configuration to bukkit format for compatibility.");
            Lang.error("You can use /ed migrate to generate new config.");
        }
    }
    public static void addItem(String key,Reward reward){
        addItem(key,reward.getItem(),reward.getChance());
    }
    public static void addItem(String key, ItemStack item, Chance chance){
        MyDragon dragon = DragonManager.mp.get(key);
        if(dragon == null) return;
        File file = getRewardFile(dragon.unique_name);
        if(file == null) return;
        FileConfiguration data = loadConfiguration(file);
        String path = "list";
        List<String> list = data.getStringList(path);
        Reward reward = new Reward(item,chance);
        list.add(reward.toString());
        data.set(path,list);
        try {
            data.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dragon.datum.add(reward);
    }
    public static void clearItem(String key){
        MyDragon dragon = DragonManager.mp.get(key);
        if(dragon == null) return;
        File file = getRewardFile(dragon.unique_name);
        if(file == null) return;
        FileConfiguration data = loadConfiguration(file);
        String path = "list";
        data.set(path,"");
        try {
            data.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dragon.datum.clear();
    }
    public static boolean removeItem(String key,int idx){
        MyDragon dragon = DragonManager.mp.get(key);
        if(dragon == null) return false;
        File file = getRewardFile(dragon.unique_name);
        if(file == null) return false;
        FileConfiguration data = loadConfiguration(file);
        String path = "list";
        List<String> list = data.getStringList(path);
        try{
            list.remove(idx);
            data.set(path,list);
            data.save(file);
            dragon.datum.remove(idx);
            return true;
        }catch (IndexOutOfBoundsException e){
            Lang.error("Index out of bound!");
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * 请勿修改获取的List
     * @return rewards of one type of dragon
     */
    public static List<Reward> getRewards(String unique_name){
        MyDragon dragon = DragonManager.mp.get(unique_name);
        if(dragon == null) return null;
        return dragon.datum;
    }
    private static File getRewardFile(String key){
        String file_path = "reward/" + key + ".yml";
        File file = new File(plugin.getDataFolder(),file_path);
        if(!file.exists()) {
            try{
                YamlConfiguration yml = new YamlConfiguration();
                if(Config.advanced_setting_backslash_split_reward) yml.options().pathSeparator('\\');
                yml.set("version", Version.reward);
                yml.set("list","");
                yml.save(file);
            }catch (IOException e){
                Lang.error("Not Found "+file_path+" ,skipped it.");
                return null;
            }
        }
        return new File(plugin.getDataFolder(),file_path);
    }
    private static YamlConfiguration loadConfiguration(@NotNull File file) {
        YamlConfiguration config = new YamlConfiguration();
        if(Config.advanced_setting_backslash_split_reward) config.options().pathSeparator('\\');
        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
        return config;
    }

}
