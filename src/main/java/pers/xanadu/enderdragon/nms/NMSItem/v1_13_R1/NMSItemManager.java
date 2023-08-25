package pers.xanadu.enderdragon.nms.NMSItem.v1_13_R1;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.MojangsonParser;
import net.minecraft.server.v1_13_R1.NBTBase;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.NMSItem.I_NMSItemManager;

public class NMSItemManager implements I_NMSItemManager {
    public org.bukkit.inventory.ItemStack readAsItem(String nbt){
        try {
            NBTTagCompound cpd = MojangsonParser.parse(nbt);
            ItemStack ei = ItemStack.a(cpd);
            return CraftItemStack.asBukkitCopy(ei);
        } catch (CommandSyntaxException e) {
            Lang.error("Wrong item nbt format:"+nbt);
            return new org.bukkit.inventory.ItemStack(Material.AIR);
        }
    }
    public org.bukkit.inventory.ItemStack cpdToItem(Object cpd){
        ItemStack ei = ItemStack.a((NBTTagCompound) cpd);
        return CraftItemStack.asBukkitCopy(ei);
    }
    public Object parseNBT(Object nbt_base){
        return CraftNBTTagConfigSerializer.serialize((NBTBase) nbt_base);
    }
    public Object readAsNBTBase(String raw){
        return CraftNBTTagConfigSerializer.v1_13_R1_b(raw);
    }
    public Object getNBTBase(Object obj){
        return CraftNBTTagConfigSerializer.deserialize(obj);
    }
}
