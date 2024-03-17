package pers.xanadu.enderdragon.nms.WorldData.v1_17_above;

import org.bukkit.World;
import pers.xanadu.enderdragon.nms.WorldData.IWorldDataManager;

public class WorldDataManager implements IWorldDataManager {
    public long getGameTime(World world){
        return world.getGameTime();
    }
}
