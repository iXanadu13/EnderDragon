package pers.xanadu.enderdragon.nms.RespawnAnchor.v1_18_above;

import org.bukkit.World;
import pers.xanadu.enderdragon.nms.RespawnAnchor.IRespawnAnchorManager;

public class RespawnAnchorManager implements IRespawnAnchorManager {
    public boolean isRespawnAnchorWorks(World world){
        return world.isRespawnAnchorWorks();
    }
}
