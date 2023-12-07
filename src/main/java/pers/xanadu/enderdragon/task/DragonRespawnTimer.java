package pers.xanadu.enderdragon.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.util.MathUtil;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class DragonRespawnTimer {
    private final String world_name;
    private final int delay;
    private BukkitRunnable runnable;
    private int remainSecond;
    private boolean isRunning;
    public DragonRespawnTimer(String WorldName,int second){
        this(WorldName,second,second);
    }
    public DragonRespawnTimer(String WorldName,int second,int rest_second){
        this.world_name = WorldName;
        this.delay = second;
        this.remainSecond = rest_second;
        this.isRunning = false;
        resetRunnable();
    }
    public void run(){
        if(!isRunning) runnable.runTaskTimerAsynchronously(plugin,0L,20L);
        isRunning = true;
    }
    public void update(){
        remainSecond = delay;
        runnable.cancel();
        isRunning = false;
        resetRunnable();
    }
    public void del(){
        if(isRunning) runnable.cancel();
    }
    public String getWorld_name(){
        return this.world_name;
    }
    public int getSetTime(){
        return this.delay;
    }
    public int getRestTime(){
        return this.remainSecond;
    }
    public boolean isRunning(){
        return isRunning;
    }
    public double getProgress(){
        return 1d*remainSecond/delay;
    }
    @Override
    public String toString(){
        return "(" + world_name + ") " +
                "remain:" + MathUtil.formatDuration(remainSecond) +
                ", set:" + MathUtil.formatDuration(delay);
    }
    private void resetRunnable(){
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                remainSecond--;
                if(remainSecond <= 0){
                    Bukkit.getScheduler().runTask(plugin,() -> {
                        if(DragonManager.canRespawn(world_name)){
                            DragonManager.initiateRespawn(world_name);
                            update();
                        }
                        else {
                            remainSecond = delay;
                            Lang.error(Lang.command_respawn_cd_retry);
                        }
                    });
                }
            }
        };
    }

}
