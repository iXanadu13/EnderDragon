package xanadu.enderdragon.gui.slots;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.GUISlot;
import xanadu.enderdragon.gui.GUISlotType;

public class DragonSlot extends GUISlot {
    private String unique_name;
    private ItemStack item;

    public DragonSlot(ConfigurationSection section) {
        super(GUISlotType.DRAGON_SLOT);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.AIR);
    }
    @Override
    public ItemStack getItemOnDisable(){
        return this.item;
    }
    public void setUnique_name(String str){
        this.unique_name = str;
    }
    public String getUnique_name(){
        return this.unique_name;
    }

    public DragonSlot(GUISlotType a, ItemStack item) {
        super(a);
        this.item = item;
    }

}
