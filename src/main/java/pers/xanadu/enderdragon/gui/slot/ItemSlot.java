package pers.xanadu.enderdragon.gui.slot;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.gui.GUISlot;
import pers.xanadu.enderdragon.gui.GUISlotType;

public class ItemSlot extends GUISlot {
    private ItemStack item;

    @Override
    public ItemStack getItem() {
        return this.item;
    }
    @Override
    public ItemStack getItemOnDisable(){
        return this.item;
    }

    public ItemSlot(ConfigurationSection section) {
        super(GUISlotType.ITEM_SLOT);
        String str = section.getString("Item");
        if(str == null){
            this.item = new ItemStack(Material.AIR);
        }
        else{
//            this.item = EnderDragon.getInstance().getItemManager().stringToItemStack(str);
        }

    }


}
