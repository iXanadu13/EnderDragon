package pers.xanadu.enderdragon.nms.NMSItem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.TestOnly;

public interface I_NMSItemManager {
    ItemStack readAsItem(String nbt);
    ItemStack cpdToItem(Object cpd);
    @TestOnly
    Object parseNBT(Object nbt_base);
    @TestOnly
    Object readAsNBTBase(String raw);
    @TestOnly
    Object getNBTBase(Object obj);
}
