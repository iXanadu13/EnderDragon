package xanadu.enderdragon.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.events.DragonDamageByPlayerEvent;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.utils.MathUtils;

public class DragonDamageByPlayerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnDragonDamageByPlayer(DragonDamageByPlayerEvent e){
        Player p = e.getDamager();
        double damage = e.getFinalDamage();
        EnderDragon dragon = e.getDragon();
        double max_health = dragon.getMaxHealth();
        double remain_health = Math.max(dragon.getHealth()-e.getFinalDamage(),0.0);
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
        return String.valueOf(MathUtils.div(d0,1,2));
    }

}
