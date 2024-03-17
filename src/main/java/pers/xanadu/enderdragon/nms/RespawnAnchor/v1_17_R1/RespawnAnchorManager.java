package pers.xanadu.enderdragon.nms.RespawnAnchor.v1_17_R1;

import net.minecraft.world.level.dimension.DimensionManager;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import pers.xanadu.enderdragon.nms.RespawnAnchor.IRespawnAnchorManager;

public class RespawnAnchorManager implements IRespawnAnchorManager {
    public boolean isRespawnAnchorWorks(World world){
        CraftWorld cw = (CraftWorld) world;
        net.minecraft.world.level.World ew = cw.getHandle();
        DimensionManager dm = ew.getDimensionManager();
        return dm.isRespawnAnchorWorks();
    }
}
