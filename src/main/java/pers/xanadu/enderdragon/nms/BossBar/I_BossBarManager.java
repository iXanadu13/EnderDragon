package pers.xanadu.enderdragon.nms.BossBar;

import org.bukkit.World;
import pers.xanadu.enderdragon.metadata.MyDragon;

import java.util.List;

public interface I_BossBarManager {
    void saveBossBarData(List<World> worlds);
    void loadBossBarData(List<World> worlds);
    void setBossBar(World world, final MyDragon myDragon);
}
