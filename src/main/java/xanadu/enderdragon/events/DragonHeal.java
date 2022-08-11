package xanadu.enderdragon.events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import static xanadu.enderdragon.EnderDragon.plugin;

public class DragonHeal implements Listener {
    @EventHandler
    public void OnDragonHeal(EntityRegainHealthEvent e){
        if(e.getEntity().getType() != EntityType.ENDER_DRAGON){return;}
        if(!e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL)){return;}
        double NormalHeal = plugin.getConfig().getDouble("normal-dragon.crystal-heal");
        double SpecialHeal = plugin.getConfig().getDouble("special-dragon.crystal-heal");
        if(e.getEntity().getScoreboardTags().contains("special")){
            e.setAmount(SpecialHeal / 2);
        }
        else{
            e.setAmount(NormalHeal / 2);
        }
    }
}
