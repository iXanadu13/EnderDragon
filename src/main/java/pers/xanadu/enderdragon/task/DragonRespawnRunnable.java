package pers.xanadu.enderdragon.task;

import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.EnderDragon;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class DragonRespawnRunnable extends BukkitRunnable {
    private final Task task;
    public DragonRespawnRunnable(Task task){
        this.task = task;
    }
    @Override
    public void run(){
        if(task == null) {
            this.cancel();
            Lang.error("The config of auto-respawn errors!Task has been disabled...");
            return;
        }
        if(task.isTimeUp()){
            new BukkitRunnable(){
                @Override
                public void run(){
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(task.world_name);
                    task.updateTime();
                    task.saveFile();
                }
            }.runTask(plugin);
        }
    }
    public void start(){
        this.runTaskTimerAsynchronously(plugin,0,200L);
    }


}
