package xanadu.enderdragon.gui.slots;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.GUISlot;
import xanadu.enderdragon.gui.GUISlotType;

public class ItemSlot extends GUISlot{
    private int i;
//    private final LoreMap<Boolean> G;
    private boolean XXXxxx;
    private ItemStack item;

    @Override
    public ItemStack getItem() {
        return this.item;
    }
    @Override
    public ItemStack getItemOnDisable(){
        return this.item;
    }

    public int getMaxAmount() {
        return this.i;
    }

//    public boolean accept(String a) {
//        if (!this.XXXxxx) {
//            return true;
//        }
//        if (this.G.get(a) != null) {
//            return true;
//        }
//        return false;
//    }

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

//    public ItemSlot() {
//        super(GUISlotType.ITEM_SLOT);
//        this.G = new LoreMap(false, false, false, false);
//        this.XXXxxx = false;
//        this.i = 64;
//    }

    public boolean acceptAll() {
        if (!this.XXXxxx) {
            return true;
        }
        return false;
    }

}
