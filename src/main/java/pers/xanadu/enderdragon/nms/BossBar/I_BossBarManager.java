package pers.xanadu.enderdragon.nms.BossBar;

import org.bukkit.World;

import java.util.List;

public interface I_BossBarManager {
    void saveBossBarData(List<World> worlds);
    void loadBossBarData(List<World> worlds);
    void setBossBar(World world,String title,String color,String style);
}
