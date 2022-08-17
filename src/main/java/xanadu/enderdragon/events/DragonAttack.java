package xanadu.enderdragon.events;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static xanadu.enderdragon.EnderDragon.*;

public class DragonAttack implements Listener {

    @EventHandler
    public void OnDragonAttack(EntityDamageByEntityEvent e){
        if(e.getDamager().getType() != EntityType.ENDER_DRAGON){return;}
        if(!isSpecial(e.getDamager())){return;}
        double multiple = plugin.getConfig().getDouble("special-dragon.damage-multiply");
        double rate = plugin.getConfig().getDouble("special-dragon.suck-blood.rate") / 100;
        double BasicSuck = plugin.getConfig().getDouble("special-dragon.suck-blood.base-suck-blood");
        boolean SuckBlood = plugin.getConfig().getBoolean("special-dragon.suck-blood.enable");
        boolean OnlyPlayer = plugin.getConfig().getBoolean("special-dragon.suck-blood.only-player");
        List<String> effects = plugin.getConfig().getStringList("special-dragon.attack-effect");
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (multiple > 0) {
                double damage = e.getDamage();
                e.setDamage(damage * multiple);
            }
            for (String str : effects) {
                if (str.length() > 0) {
                    String type = str.substring(0, str.indexOf(" "));
                    int time = Integer.parseInt(str.substring(str.indexOf(" ") + 1, str.lastIndexOf(" ")));
                    int level = Integer.parseInt(str.substring(str.lastIndexOf(" ") + 1));
                    PotionEffectType effectType = PotionEffectType.getByName(type.toUpperCase());
                    if (effectType != null) {
                        p.addPotionEffect(new PotionEffect(effectType, time * 20, level - 1));
                    }
                }
            }
            if(!SuckBlood){return;}
            double FinalDamage = e.getFinalDamage();
            EnderDragon dragon = (EnderDragon) e.getDamager();
            double health = dragon.getHealth()+FinalDamage*rate+BasicSuck;
            if(health > dragon.getMaxHealth()){health = dragon.getMaxHealth();}
            dragon.setHealth(health);

        }
        else{
            if((!SuckBlood) || OnlyPlayer){return;}
            double FinalDamage = e.getFinalDamage();
            EnderDragon dragon = (EnderDragon) e.getDamager();
            double health = dragon.getHealth()+FinalDamage*rate+BasicSuck;
            if(health > dragon.getMaxHealth()){health = dragon.getMaxHealth();}
            dragon.setHealth(health);
        }

    }
}
