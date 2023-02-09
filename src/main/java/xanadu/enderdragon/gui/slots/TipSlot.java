package xanadu.enderdragon.gui.slots;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xanadu.enderdragon.gui.GUISlot;
import xanadu.enderdragon.gui.GUISlotType;

import static xanadu.enderdragon.manager.ItemManager.readAsItem;

public class TipSlot extends GUISlot {
    private ItemStack item;
    private ItemStack itemOnDisable;
    public TipSlot(GUISlotType slotType, Material material, String str) {
        super(slotType);
        this.item = new ItemStack(material);
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(str);
        this.item.setItemMeta(meta);
    }

    public TipSlot(GUISlotType slotType, ConfigurationSection section) {
        super(slotType);
        this.item = readAsItem(section,"data");
        if(slotType == GUISlotType.PAGE_PREV || slotType == GUISlotType.PAGE_NEXT){
            this.itemOnDisable = readAsItem(section,"data_disable");
        }
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }
    @Override
    public ItemStack getItemOnDisable(){
        return this.itemOnDisable;
    }

    public TipSlot(GUISlotType a, ItemStack item) {
        super(a);
        this.item = item;
    }
}
