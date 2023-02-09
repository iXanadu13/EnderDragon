package xanadu.enderdragon.gui.slots;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.gui.GUISlotType;

import static xanadu.enderdragon.manager.ItemManager.readAsItem;

public class PageJumpSlot extends TipSlot{
    private final String name;
    private ItemStack item;

    public String getGuiName() {
        return this.name;
    }

    public PageJumpSlot(ConfigurationSection section) {
        super(GUISlotType.PAGE_JUMP, section);
        this.name = section.getString("gui");
        this.item = readAsItem(section,"data");
    }
    @Override
    public ItemStack getItem() {
        return this.item;
    }
}
