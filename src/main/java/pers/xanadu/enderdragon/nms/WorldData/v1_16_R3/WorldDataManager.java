package pers.xanadu.enderdragon.nms.WorldData.v1_16_R3;

import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import pers.xanadu.enderdragon.nms.WorldData.IWorldDataManager;

public class WorldDataManager implements IWorldDataManager {
    public long getGameTime(World world){
        CraftWorld cw = (CraftWorld) world;
        WorldServer ws = cw.getHandle();
        return ws.getTime();
    }
}
