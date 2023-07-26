package pers.xanadu.enderdragon.gui.slot;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.gui.GUISlot;
import pers.xanadu.enderdragon.gui.GUISlotType;

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
