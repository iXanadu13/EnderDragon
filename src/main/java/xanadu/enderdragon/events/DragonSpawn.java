package xanadu.enderdragon.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scoreboard.Team;
import xanadu.enderdragon.lang.Message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static xanadu.enderdragon.EnderDragon.*;

public class DragonSpawn implements Listener {

    @EventHandler
    public void OnDragonSpawn(CreatureSpawnEvent e) throws IOException {
        if(e.getEntity().getType() != EntityType.ENDER_DRAGON){return;}
        int times = data.getInt("times");
        double health = plugin.getConfig().getDouble("special-dragon.max-health");
        double SpawnHealth = plugin.getConfig().getDouble("special-dragon.spawn-health");
        int circle = plugin.getConfig().getInt("special-dragon.respawn-circle");
        if (circle == 0 ){circle = 999999999;}
        boolean chance = plugin.getConfig().getInt("special-dragon.chance") > ThreadLocalRandom.current().nextInt(0, 100);
        boolean SpecialMsg0 = plugin.getConfig().getBoolean("special-dragon.spawn-remind");
        String SpawnMsg = Message.DragonSpawnBroadcast;
        String SpecialMsg = Message.SpecialBroadcast;
        String Name = plugin.getConfig().getString("special-dragon.name");
        String NormalName = plugin.getConfig().getString("normal-dragon.name");
        String color = plugin.getConfig().getString("special-dragon.glow-color");
        if(SpawnMsg == null){SpawnMsg = "none";}
        if(!SpawnMsg.equals("none")){
            Bukkit.broadcastMessage(prefix + SpawnMsg.replaceAll("%times%", String.valueOf(times)));
        }
        if(times % circle == 0 && chance) {
            if(mcMainVersion >= 11){e.getEntity().addScoreboardTag("special");}
            else{
                List<String> special = data.getStringList("special");
                special.add(e.getEntity().getUniqueId().toString());
                data.set("special",special);
                data.save(dataF);
            }
            if(health > 0) {
                e.getEntity().setMaxHealth(health);
            }
            e.getEntity().setHealth(SpawnHealth);
            if (color != null && mcMainVersion >= 9) {
                if(!color.equalsIgnoreCase("disable")) {
                    color = color.toUpperCase();
                    if(server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow") == null) {
                        server.getScoreboardManager().getMainScoreboard().registerNewTeam("enderdragon-glow");
                    }
                    Team team = server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow");
                    if(mcMainVersion >= 12) {
                        team.setColor(ChatColor.valueOf(color));
                    }
                    else{
                        team.setPrefix(ChatColor.valueOf(color).toString());
                    }
                    team.addEntry(e.getEntity().getUniqueId().toString());
                    e.getEntity().setGlowing(true);
                }
            }
            e.getEntity().setCustomName(Name);
            if(SpecialMsg0 && SpecialMsg != null){
                Bukkit.broadcastMessage(prefix + SpecialMsg);
            }
        }
        else{
            e.getEntity().setCustomName(NormalName);
        }
    }
}
