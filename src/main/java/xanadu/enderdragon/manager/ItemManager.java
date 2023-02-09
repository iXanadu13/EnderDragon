package xanadu.enderdragon.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xanadu.enderdragon.gui.Reward;
import xanadu.enderdragon.utils.Chance;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ItemManager {
    public static ConfigurationSection write(Reward reward){
        Chance chance = reward.getChance();
        return write(reward.getItem(),chance.getValue(),chance.getStr(),reward.getName());
    }
    public static ConfigurationSection write(ItemStack item,double value,String str,String name){
        YamlConfiguration yaml = new YamlConfiguration();
        if(name == null){
            ItemMeta meta = item.getItemMeta();
            if(meta != null) name = meta.getDisplayName();
            else name = item.getType().name() + System.currentTimeMillis();
        }
        yaml.set(name + ".data", item);
        yaml.set(name + ".drop_chance.value",value);
        yaml.set(name + ".drop_chance.format",str);
        return yaml;
    }
    public static Reward readAsReward(ConfigurationSection section){
        Set<String> strings = section.getKeys(false);
        String name = strings.iterator().next();
        ConfigurationSection section0 = section.getConfigurationSection(name);
        if(section0 == null) return null;
        ItemStack item;
        String nbt = section0.getString("data");
        if (nbt == null) item = new ItemStack(Material.AIR);
        else item = section0.getItemStack("data");
        double d0 = section0.getDouble("drop_chance.value");
        String str = section0.getString("drop_chance.format");
        return new Reward(item,new Chance(d0, str));
    }
    public static ItemStack readAsItem(ConfigurationSection section, String path){
//        if(!isValid(section)) {
//            Bukkit.getServer().shutdown();
//            return new ItemStack(Material.AIR);
//        }
        String nbt = section.getString(path);
        if (nbt == null) return new ItemStack(Material.AIR);
//        if (!isValid(nbt)) return new ItemStack(Material.AIR);
        return section.getItemStack(path);
    }
    public static void addLore(ItemStack item, String lore){
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            List<String> lores = meta.getLore();
            if (lores != null) {
                lores.add(0,lore);
                meta.setLore(lores);
            }
            else {
                meta.setLore(Collections.singletonList(lore));
            }
            item.setItemMeta(meta);
        }
    }
    public static boolean isEmpty(ItemStack item){
        if(item == null) return true;
        if(item.getType() == Material.AIR) return true;
        return false;
    }
//    public static ItemStack StringToItemStack(String str) {
//        YamlConfiguration yml = new YamlConfiguration();
//        ItemStack item;
//        try {
//            yml.loadFromString(str);
//            item = yml.getItemStack("item");
//        } catch (InvalidConfigurationException ex) {
//            item = new ItemStack(Material.AIR, 1);
//        }
//        return item;
//    }
//    public Reward loadFromFile(ConfigurationSection section){
//        ItemStack item;
//        if(section.getString("item.data") == null || section.getString("item.drop_chance") == null){
//            return new Reward(new ItemStack(Material.AIR),);
//        }
//        try {
//            yamlManager.loadFromString(section.getString("item."));
//        }
//    }
    public static ItemStack read(ConfigurationSection section, String str, String str2, Material material){
        if(material == null) return new ItemStack(Material.AIR);
        return new ItemStack(material,1);

    }
}
