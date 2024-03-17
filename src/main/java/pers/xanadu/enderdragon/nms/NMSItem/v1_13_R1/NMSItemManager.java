package pers.xanadu.enderdragon.nms.NMSItem.v1_13_R1;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.MojangsonParser;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.NMSItem.INMSItemManager;

public class NMSItemManager implements INMSItemManager {
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
}
