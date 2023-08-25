package pers.xanadu.enderdragon.nms.WorldData.v1_17_above;

import org.bukkit.World;
import pers.xanadu.enderdragon.nms.WorldData.I_WorldDataManager;

public class WorldDataManager implements I_WorldDataManager {
    public long getGameTime(World world){
        return world.getGameTime();
    }
}
