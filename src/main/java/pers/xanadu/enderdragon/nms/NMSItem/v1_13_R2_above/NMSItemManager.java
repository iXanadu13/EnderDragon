package pers.xanadu.enderdragon.nms.NMSItem.v1_13_R2_above;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.NMSItem.I_NMSItemManager;

public class NMSItemManager implements I_NMSItemManager {
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
    public Object parseNBT(Object nbt_base){
        return EnderDragon.getInstance().getNMSManager().serializeNBTBase(nbt_base);
    }
    public Object readAsNBTBase(String raw){
        return EnderDragon.getInstance().getNMSManager().StringParseLiteral(raw);
    }
    public Object getNBTBase(Object obj){
        return EnderDragon.getInstance().getNMSManager().deserializeObject(obj);
    }
}
