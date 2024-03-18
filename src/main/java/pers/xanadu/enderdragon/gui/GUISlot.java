package pers.xanadu.enderdragon.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.gui.slot.*;

public abstract class GUISlot {
    private final GUISlotType guiSlotType;
    protected DataType data_type;
    public abstract ItemStack getItem();
    public abstract ItemStack getItemOnDisable();
    protected GUISlot(GUISlotType type){
        this.guiSlotType = type;
    }
    public final GUISlotType getType(){
        return this.guiSlotType;
    }
    public static GUISlot parse(ConfigurationSection section){
        if(section == null) return new EmptySlot();
        GUISlotType guiSlotType = GUISlotType.getByName(section.getString("type"));
        switch (guiSlotType) {
            case ITEM_SLOT : {
                return new ItemSlot(section);
            }
            case DRAGON_SLOT : {
                return new DragonSlot(section);
            }
            case PAGE_PREV :
            case PAGE_NEXT :
            case PAGE_TIP :
            case TIP : {
                return new TipSlot(guiSlotType, section);
            }
            case PAGE_JUMP : {
                return new PageJumpSlot(section);
            }
        }
        return new EmptySlot();
    }
    protected enum DataType{
        DEFAULT,NBT,ADVANCED,SIMPLE;
        public static DataType fromString(String data_type){
            if (data_type == null) return DEFAULT;
            switch (data_type){
                case "nbt": {
                    return NBT;
                }
                case "advanced": {
                    return ADVANCED;
                }
                case "simple": {
                    return SIMPLE;
                }
                default: {
                    return DEFAULT;
                }
            }
        }
    }
}
