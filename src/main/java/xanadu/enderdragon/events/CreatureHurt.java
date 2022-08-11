package xanadu.enderdragon.events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static xanadu.enderdragon.EnderDragon.*;

public class CreatureHurt implements Listener {
    @EventHandler
    public void OnCreatureHurt(EntityDamageByEntityEvent e){
        int DamageVisual = plugin.getConfig().getInt("special-dragon.damage-visible");
        String message = language.getString("damage-display");
        if(DamageVisual == 0){return;}
        if(e.getDamager().getType() != EntityType.PLAYER){return;}
        if(e.getEntity().getType() != EntityType.ENDER_DRAGON){return;}
        Player p = (Player) e.getDamager();
        double damage = e.getDamage();
        message = message.replaceAll("%damage%", String.valueOf(damage));
        if(DamageVisual == 1){p.sendTitle("",message,5,40,5);}
        if(DamageVisual == 2){p.sendMessage(message);}
        if(DamageVisual == 3){p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));}
    }
}
