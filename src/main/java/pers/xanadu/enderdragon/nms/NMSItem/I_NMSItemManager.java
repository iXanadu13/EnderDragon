package pers.xanadu.enderdragon.nms.NMSItem;

import org.bukkit.inventory.ItemStack;

public interface I_NMSItemManager {
    ItemStack readAsItem(String nbt);
    ItemStack cpdToItem(Object cpd);
    Object parseNBT(Object nbt_base);
    Object readAsNBTBase(String raw);
    Object getNBTBase(Object obj);
}
