package pers.xanadu.enderdragon.gui.slot;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.xanadu.enderdragon.gui.GUISlot;
import pers.xanadu.enderdragon.gui.GUISlotType;

import static pers.xanadu.enderdragon.manager.ItemManager.*;

public class TipSlot extends GUISlot {
    private ItemStack item;
    protected boolean hasDisableMode;
    protected ItemStack itemOnDisable;

    public TipSlot(GUISlotType slotType, Material material, String str) {
        super(slotType);
        this.item = new ItemStack(material);
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(str);
        this.item.setItemMeta(meta);
    }

    public TipSlot(GUISlotType slotType, ConfigurationSection section) {
        super(slotType);
        if(slotType == GUISlotType.PAGE_PREV || slotType == GUISlotType.PAGE_NEXT){
            this.hasDisableMode = true;
        }
        else this.hasDisableMode = false;
        if(hasDisableMode){
            if(data_type == DataType.NBT) this.itemOnDisable = readFromNBT(section,"data_disable");
            else this.itemOnDisable = readFromBukkit(section,"data_disable");
        }
        String data_type = section.getString("data_type");
        if("nbt".equals(data_type)) {
            this.data_type = DataType.NBT;
            this.item = readFromNBT(section,"data");
        }
        else if("advanced".equals(data_type)){
            this.data_type = DataType.ADVANCED;
            this.item = readFromAdvData(section,"data");
        }
        else {
            this.data_type = DataType.DEFAULT;
            this.item = readFromBukkit(section,"data");
        }
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }
    @Override
    public ItemStack getItemOnDisable(){
        if(hasDisableMode) return this.itemOnDisable;
        return this.item;
    }

    public TipSlot(GUISlotType a, ItemStack item) {
        super(a);
        this.item = item;
    }
}
