package pers.xanadu.enderdragon.nms.RespawnAnchor.v1_18_above;

import org.bukkit.World;
import pers.xanadu.enderdragon.nms.RespawnAnchor.I_RespawnAnchorManager;

public class RespawnAnchorManager implements I_RespawnAnchorManager {
    public boolean isRespawnAnchorWorks(World world){
        return world.isRespawnAnchorWorks();
    }
}
