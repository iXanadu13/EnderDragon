package pers.xanadu.enderdragon.nms.WorldData.v1_13_R1;

import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import pers.xanadu.enderdragon.nms.WorldData.I_WorldDataManager;

public class WorldDataManager implements I_WorldDataManager {
    public long getGameTime(World world){
        CraftWorld cw = (CraftWorld) world;
        WorldServer ws = cw.getHandle();
        return ws.getTime();
    }
}
