package pers.xanadu.enderdragon.hook.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.TimerManager;
import pers.xanadu.enderdragon.task.DragonRespawnTimer;
import pers.xanadu.enderdragon.util.MathUtil;

public class Papi extends PlaceholderExpansion {
    private final EnderDragon plugin;

    public Papi(EnderDragon plugin) {
        this.plugin = plugin;
        register();
    }

    @Override
    public @NotNull String getAuthor() {
        return "Xanadu13";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ed";
    }

    @Override
    public @NotNull String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public boolean canRegister(){
        return true;
    }
    @Override
    public String onPlaceholderRequest(final Player player, final @NotNull String params){
        if(params.equals("can_respawn")){
            if(player == null) return String.valueOf(false);
            return String.valueOf(DragonManager.canRespawn(player.getWorld()));
        }
        if(params.equals("respawn_cd_progress")){
            if(player == null) return null;
            World world = player.getWorld();
            DragonRespawnTimer timer = TimerManager.getTimer(world.getName());
            if(timer == null) return "null";
            return String.valueOf(timer.getProgress());
        }
        if(params.equals("respawn_cd_remainTime")){
            if(player == null) return null;
            World world = player.getWorld();
            DragonRespawnTimer timer = TimerManager.getTimer(world.getName());
            if(timer == null) return "null";
            return String.valueOf(timer.getRestTime());
        }
        if(params.equals("respawn_cd_setTime")){
            if(player == null) return null;
            World world = player.getWorld();
            DragonRespawnTimer timer = TimerManager.getTimer(world.getName());
            if(timer == null) return "null";
            return String.valueOf(timer.getSetTime());
        }
        if(params.startsWith("can_respawn_")){
            String name = params.substring("can_respawn_".length());
            World world = Bukkit.getWorld(name);
            return String.valueOf(DragonManager.canRespawn(world));
        }
        if(params.startsWith("respawn_cd_progress_")){
            String name = params.substring("respawn_cd_progress_".length());
            DragonRespawnTimer timer = TimerManager.getTimer(name);
            if(timer == null) return "null";
            return String.valueOf(timer.getProgress());
        }
        if(params.startsWith("respawn_cd_remainTime_")){
            String name = params.substring("respawn_cd_remainTime_".length());
            DragonRespawnTimer timer = TimerManager.getTimer(name);
            if(timer == null) return "null";
            return String.valueOf(timer.getRestTime());
        }
        if(params.startsWith("respawn_cd_setTime_")){
            String name = params.substring("respawn_cd_setTime_".length());
            DragonRespawnTimer timer = TimerManager.getTimer(name);
            if(timer == null) return "null";
            return String.valueOf(timer.getSetTime());
        }
        if(params.startsWith("respawn_cd_remain_")){
            String sub = params.substring("respawn_cd_remain_".length());
            String[] splits = sub.split("\\$",5);
            DragonRespawnTimer timer = TimerManager.getTimer(splits[0]);
            if(timer == null) return "null";
            if(splits.length==1) return MathUtil.formatDuration(timer.getRestTime());
            if(splits.length==5){
                int[] values = MathUtil.getDate(timer.getRestTime());
                return values[0]+splits[1]+values[1]+splits[2]+values[2]+splits[3]+values[3]+splits[4];
            }
            return "wrong format";
        }
        return null;
    }
    @Override
    public String onRequest(final OfflinePlayer player, final @NotNull String params) {
        if(player == null) return onPlaceholderRequest(null,params);
        return onPlaceholderRequest(player.getPlayer(),params);
    }
}
