package pers.xanadu.enderdragon.nms.NMSItem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.TestOnly;

public interface INMSItemManager {
    ItemStack readAsItem(String nbt);
    ItemStack cpdToItem(Object cpd);
}
