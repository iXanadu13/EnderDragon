package pers.xanadu.enderdragon.gui.slot;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.xanadu.enderdragon.gui.GUISlot;
import pers.xanadu.enderdragon.gui.GUISlotType;
import pers.xanadu.enderdragon.manager.ItemManager;

import static pers.xanadu.enderdragon.manager.ItemManager.*;

public class TipSlot extends GUISlot {
    private final ItemStack item;
    protected boolean hasDisableMode;
    protected ItemStack itemOnDisable;

    public TipSlot(GUISlotType slotType, Material material, String str) {
        super(slotType);
        this.item = new ItemStack(material);
        ItemMeta meta = this.item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(str);
        this.item.setItemMeta(meta);
    }

    public TipSlot(GUISlotType slotType, ConfigurationSection section) {
        super(slotType);
        if(slotType == GUISlotType.PAGE_PREV || slotType == GUISlotType.PAGE_NEXT){
            this.hasDisableMode = true;
        }
        else this.hasDisableMode = false;
        this.data_type = DataType.fromString(section.getString("data_type"));
        switch (data_type) {
            case NBT:
                ItemManager.getLegacy().compareAndSet(false,true);
                this.item = readFromNBT(section,"data");
                if(hasDisableMode) this.itemOnDisable = readFromNBT(section,"data_disable");
                break;
            case ADVANCED:
                ItemManager.getLegacy().compareAndSet(false,true);
                this.item = readFromAdvData(section,"data");
                if(hasDisableMode) this.itemOnDisable = readFromAdvData(section,"data_disable");
                break;
            case SIMPLE:
                this.item = readFromSimple(section,"data");
                if(hasDisableMode) this.itemOnDisable = readFromSimple(section,"data_disable");
                break;
            default:
                this.item = readFromBukkit(section,"data");
                if(hasDisableMode) this.itemOnDisable = readFromBukkit(section,"data_disable");
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
