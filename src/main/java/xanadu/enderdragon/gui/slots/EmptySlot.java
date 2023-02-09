package xanadu.enderdragon.gui.slots;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.GUISlot;
import xanadu.enderdragon.gui.GUISlotType;

public class EmptySlot extends GUISlot {
    @Override
    public ItemStack getItem(){
        return new ItemStack(Material.AIR);
    }
    @Override
    public ItemStack getItemOnDisable(){
        return new ItemStack(Material.AIR);
    }
    public EmptySlot(){
        super(GUISlotType.EMPTY);
    }
}
