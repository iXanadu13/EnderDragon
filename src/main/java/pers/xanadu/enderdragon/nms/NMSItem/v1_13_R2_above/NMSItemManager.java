package pers.xanadu.enderdragon.nms.NMSItem.v1_13_R2_above;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.NMSItem.INMSItemManager;

public class NMSItemManager implements INMSItemManager {
    public ItemStack readAsItem(String nbt){
        try {
            return EnderDragon.getInstance().getNMSManager().getItemStack(nbt);
        } catch (Throwable e) {
            Lang.error("Wrong item nbt format:"+nbt);
            return new org.bukkit.inventory.ItemStack(Material.AIR);
        }
    }
    public ItemStack cpdToItem(Object cpd){
        return EnderDragon.getInstance().getNMSManager().getItemStack(cpd);
    }
}
