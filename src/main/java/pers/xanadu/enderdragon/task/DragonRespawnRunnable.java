package pers.xanadu.enderdragon.task;

import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.TaskManager;
import pers.xanadu.enderdragon.EnderDragon;

import static pers.xanadu.enderdragon.EnderDragon.plugin;

public class DragonRespawnRunnable extends BukkitRunnable {
    private static DragonRespawnRunnable runnable = null;
    @Override
    public void run(){
        if(TaskManager.task == null) {
            this.cancel();
            runnable = null;
            Lang.error("The config of auto-respawn errors!Task has been disabled...");
            return;
        }
        if(TaskManager.task.isTimeUp()){
            new BukkitRunnable(){
                @Override
                public void run(){
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(Config.auto_respawn_world_the_end_name);
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
        runnable = new DragonRespawnRunnable();
        runnable.runTaskTimerAsynchronously(plugin,0,200L);
        Lang.info("The task has started to run...");
    }
}
