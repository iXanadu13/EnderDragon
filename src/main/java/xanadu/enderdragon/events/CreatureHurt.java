package xanadu.enderdragon.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xanadu.enderdragon.lang.Message;

import static xanadu.enderdragon.EnderDragon.*;

public class CreatureHurt implements Listener {
    @EventHandler
    public void OnCreatureHurt(EntityDamageByEntityEvent e){
        int DamageVisual = plugin.getConfig().getInt("special-dragon.damage-visible");
        String MSG = Message.DamageDisplay;
        if(DamageVisual == 0){return;}
        if(e.getDamager().getType() != EntityType.PLAYER){return;}
        if(e.getEntity().getType() != EntityType.ENDER_DRAGON){return;}
        Player p = (Player) e.getDamager();
        double damage = e.getDamage();
        MSG = MSG.replaceAll("%damage%", String.valueOf(damage));
        if(DamageVisual == 1){p.sendTitle("",MSG,5,40,5);}
        if(DamageVisual == 2){p.sendMessage(MSG);}
        if(DamageVisual == 3){p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MSG));}
    }
}
