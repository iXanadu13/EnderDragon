package xanadu.enderdragon.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scoreboard.Team;
import xanadu.enderdragon.lang.Message;

import java.util.concurrent.ThreadLocalRandom;

import static xanadu.enderdragon.EnderDragon.*;

public class DragonSpawn implements Listener {

    @EventHandler
    public void OnDragonSpawn(CreatureSpawnEvent e){
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
            e.getEntity().addScoreboardTag("special");
            if(health > 0) {
                AttributeInstance MaxHealth = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
                assert MaxHealth != null;
                MaxHealth.addModifier(new AttributeModifier("最大生命值", health - 200, AttributeModifier.Operation.ADD_NUMBER));
            }
            e.getEntity().setHealth(SpawnHealth);
            if (color != null) {
                if(!color.equalsIgnoreCase("disable")) {
                    color = color.toUpperCase();
                    if(server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow") == null) {
                        server.getScoreboardManager().getMainScoreboard().registerNewTeam("enderdragon-glow");
                    }
                    Team team = server.getScoreboardManager().getMainScoreboard().getTeam("enderdragon-glow");
                    team.setColor(ChatColor.valueOf(color));
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
