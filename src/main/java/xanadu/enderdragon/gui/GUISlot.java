package xanadu.enderdragon.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.slots.*;

public abstract class GUISlot {
    private final GUISlotType guiSlotType;
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
            case ITEM_SLOT -> {
                return new ItemSlot(section);
            }
            case DRAGON_SLOT -> {
                return new DragonSlot(section);
            }
            case PAGE_PREV, PAGE_NEXT, PAGE_TIP, TIP -> {
                return new TipSlot(guiSlotType, section);
            }
            case PAGE_JUMP -> {
                return new PageJumpSlot(section);
            }
        }
        return new EmptySlot();
    }
}
