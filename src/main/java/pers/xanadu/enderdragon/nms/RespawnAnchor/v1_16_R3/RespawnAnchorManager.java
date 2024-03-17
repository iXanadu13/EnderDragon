package pers.xanadu.enderdragon.nms.RespawnAnchor.v1_16_R3;

import net.minecraft.server.v1_16_R3.DimensionManager;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import pers.xanadu.enderdragon.nms.RespawnAnchor.IRespawnAnchorManager;

public class RespawnAnchorManager implements IRespawnAnchorManager {
    public boolean isRespawnAnchorWorks(World world){
        CraftWorld cw = (CraftWorld) world;
        net.minecraft.server.v1_16_R3.World ew = cw.getHandle();
        DimensionManager dm = ew.getDimensionManager();
        return dm.isRespawnAnchorWorks();
    }
}
