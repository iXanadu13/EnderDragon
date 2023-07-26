package pers.xanadu.enderdragon.nms.NMSItem.v1_12_R1;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.NMSItem.I_NMSItemManager;

public class NMSItemManager implements I_NMSItemManager {

    public org.bukkit.inventory.ItemStack readAsItem(String nbt){
        try {
            NBTTagCompound cpd = MojangsonParser.parse(nbt);
            ItemStack ei = new ItemStack(cpd);
            return CraftItemStack.asBukkitCopy(ei);
        } catch (MojangsonParseException e) {
            Lang.error("Wrong item nbt format:"+nbt);
            return new org.bukkit.inventory.ItemStack(Material.AIR);
        }
    }
    public org.bukkit.inventory.ItemStack cpdToItem(Object cpd){
        ItemStack ei = new ItemStack((NBTTagCompound) cpd);
        return CraftItemStack.asBukkitCopy(ei);
    }
    public Object parseNBT(Object nbt_base){
        return CraftNBTTagConfigSerializer.serialize((NBTBase) nbt_base);
    }
    public Object readAsNBTBase(String raw){
        return CraftNBTTagConfigSerializer.v1_12_R1_c(raw);
    }
    public Object getNBTBase(Object obj){
        return CraftNBTTagConfigSerializer.deserialize(obj);
    }

}
