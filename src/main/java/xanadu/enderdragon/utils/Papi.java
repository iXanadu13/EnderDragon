package xanadu.enderdragon.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import xanadu.enderdragon.EnderDragon;

public class Papi extends PlaceholderExpansion {
    private final EnderDragon plugin;

    public Papi(EnderDragon plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Xanadu13";
    }

    @Override
    public String getIdentifier() {
        return "ed";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
//        if(!Config.BossBar_Enable) return null;
//        if(params.equalsIgnoreCase("bossbar_progress")) {
//            if(player.isOnline() && groups.get(player.getUniqueId())!=null){
//                double progress = groups.get(player.getUniqueId()).getProgress();
//                return String.valueOf(div(progress,1,3));
//            }
//            return String.valueOf(0);
//        }
//        else if(params.equalsIgnoreCase("remain_time")){
//            if(player.isOnline() && groups.get(player.getUniqueId())!=null){
//                return String.valueOf(groups.get(player.getUniqueId()).getRemainTime());
//            }
//            return String.valueOf(0);
//        }
        return null;
    }
}
