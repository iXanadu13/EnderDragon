package xanadu.enderdragon.manager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.Reward;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.utils.Chance;
import xanadu.enderdragon.utils.MyDragon;

import java.io.IOException;
import java.util.List;

import static xanadu.enderdragon.EnderDragon.data;
import static xanadu.enderdragon.EnderDragon.dataF;
import static xanadu.enderdragon.manager.DragonManager.dragons;
import static xanadu.enderdragon.manager.DragonManager.mp;
import static xanadu.enderdragon.manager.ItemManager.readAsReward;

public class RewardManager {
    public static void reload(){
        for(MyDragon dragon : dragons){
            dragon.datum.clear();
            String path = dragon.unique_name;
            List<String> list = data.getStringList(path);
            for(String str : list){
                YamlConfiguration yml = new YamlConfiguration();
                try{
                    yml.loadFromString(str);
                    Reward reward = readAsReward(yml);
                    dragon.datum.add(reward);
                }catch (InvalidConfigurationException e){
                    Lang.error(Lang.plugin_item_read_error + str);
                }
            }
        }
    }
    public static void addItem(String key,Reward reward){
        addItem(key,reward.getItem(),reward.getChance());
    }
    public static void addItem(String key, ItemStack item, Chance chance){
        MyDragon dragon = mp.get(key);
        if(dragon == null) return;
        String path = dragon.unique_name;
        List<String> list = data.getStringList(path);
        Reward reward = new Reward(item,chance);
        list.add(reward.toString());
        data.set(path,list);
        try {
            data.save(dataF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = YamlConfiguration.loadConfiguration(dataF);
        dragon.datum.add(reward);
    }
    public static void clearItem(String key){
        MyDragon dragon = mp.get(key);
        if(dragon == null) return;
        String path = dragon.unique_name;
        data.set(path,"");
        try {
            data.save(dataF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        data = YamlConfiguration.loadConfiguration(dataF);
        dragon.datum.clear();
    }
}
