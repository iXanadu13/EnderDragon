package xanadu.enderdragon.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xanadu.enderdragon.EnderDragon;
import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.manager.TaskManager;

import static xanadu.enderdragon.EnderDragon.plugin;
import static xanadu.enderdragon.config.Lang.*;

public class RespawnDragonRunnable extends BukkitRunnable {
    private static RespawnDragonRunnable runnable = null;
    @Override
    public void run(){
        if(TaskManager.task == null) {
            this.cancel();
            runnable = null;
            error("The config of auto-respawn errors!Task has been disabled...");
            return;
        }
        if(TaskManager.task.isTimeUp()){
            new BukkitRunnable(){
                @Override
                public void run(){
                    boolean f = EnderDragon.getInstance().getDragonManager().initiateRespawn(Bukkit.getWorld(Config.auto_respawn_world_the_end_name));
                    if(f) Lang.broadcastMSG(Lang.dragon_auto_respawn);
                }
            }.runTask(plugin);
            TaskManager.task.updateTime();
        }
    }
    public static void reload(){
        if(runnable != null){
            runnable.cancel();
            runnable = null;
        }
        runnable = new RespawnDragonRunnable();
        runnable.runTaskTimerAsynchronously(plugin,0,200L);
        info("The task has started to run...");
    }
}
