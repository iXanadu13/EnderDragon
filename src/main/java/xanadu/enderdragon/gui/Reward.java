package xanadu.enderdragon.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xanadu.enderdragon.utils.Chance;

import static xanadu.enderdragon.manager.TaskManager.getCurrentTimeWithSpecialFormat;


public class Reward extends ItemStack {
    protected String name;
    protected Chance chance;
    private ItemStack item;

    @Override
    public String toString(){
        YamlConfiguration yaml = new YamlConfiguration();
        if(name == null){
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                String displayName = meta.getDisplayName();
                if("".equals(displayName) || displayName == null){
                    name = item.getType().name().toLowerCase() + "(" + getCurrentTimeWithSpecialFormat() + ")";
                }
                else name = meta.getDisplayName();
            }
            else name = item.getType().name().toLowerCase() + "(" + getCurrentTimeWithSpecialFormat() + ")";
        }


        yaml.set(name + ".data", item);
        yaml.set(name + ".drop_chance.value",chance.getValue());
        yaml.set(name + ".drop_chance.format",chance.getStr());
        return yaml.saveToString();
    }

    public String getName() {
        return this.name;
    }
//
//    @Override
//    public int hashCode() {
//        return this.name.hashCode();
//    }

    public ItemStack getItem() {
        if(item == null) return new ItemStack(Material.AIR);
        return item.clone();
    }
    public Chance getChance(){
        return this.chance;
    }



    protected Reward(ItemStack item,String chance) {
        this.item = item;

    }
    public Reward(ItemStack item, Chance chance){
        this.item = item;
        this.chance = chance;
    }
    public Reward(ItemStack item){
        this.item = item;
        this.chance = null;
    }

}
