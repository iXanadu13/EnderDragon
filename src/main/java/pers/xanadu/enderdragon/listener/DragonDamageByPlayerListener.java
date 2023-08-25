package pers.xanadu.enderdragon.listener;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.event.DragonDamageByPlayerEvent;
import pers.xanadu.enderdragon.manager.DamageManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DragonDamageByPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDragonDamageByPlayer(final DragonDamageByPlayerEvent e){
        Player p = e.getDamager();
        EnderDragon dragon = e.getDragon();
        double health = dragon.getHealth();
        double damage = Math.min(e.getFinalDamage(),health);
        if(damage > 0.0d){
            UUID dragon_uid = dragon.getUniqueId();
            ConcurrentHashMap<String,Double> mp = DamageManager.data.computeIfAbsent(dragon_uid, k->new ConcurrentHashMap<>());
            mp.compute(p.getName(),(k,v)->v==null?damage:v+damage);
        }
        //Bukkit.broadcastMessage(RewardManager.data.get(dragon.getUniqueId()).get(p.getUniqueId())+"");
        double max_health = dragon.getMaxHealth();
        double remain_health = Math.max(health-e.getFinalDamage(),0.0);
        String str = Lang.dragon_damage_display.replaceAll("%damage%", format(damage)).replaceAll("%remain_health%",format(remain_health)).replaceAll("%max_health%",format(max_health));
        switch (Config.damage_visible_mode.toLowerCase()){
            case "actionbar" : {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(str));
                break;
            }
            case "chatbox" : {
                Lang.sendFeedback(p,str);
                break;
            }
            case "subtitle" : {
                p.sendTitle("",str,5,40,5);
                break;
            }
            default : {

            }

        }
    }

    private String format(double d0){
        return String.format("%.2f",d0);
    }

}

