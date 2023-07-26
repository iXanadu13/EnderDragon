package pers.xanadu.enderdragon.reward;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.manager.ItemManager;


public class Reward extends ItemStack {
    protected String name;
    protected Chance chance;
    private ItemStack item;

    @Override
    public String toString(){
        return ItemManager.write(this);
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
