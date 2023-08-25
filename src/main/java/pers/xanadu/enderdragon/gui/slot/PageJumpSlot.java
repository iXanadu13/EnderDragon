package pers.xanadu.enderdragon.gui.slot;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.gui.GUISlotType;

public class PageJumpSlot extends TipSlot{
    private final String name;
    public String getGuiName() {
        return this.name;
    }
    public PageJumpSlot(ConfigurationSection section) {
        super(GUISlotType.PAGE_JUMP, section);
        this.name = section.getString("gui");
    }
    @Override
    public ItemStack getItemOnDisable(){
        return this.itemOnDisable;
    }

}
