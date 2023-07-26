package pers.xanadu.enderdragon.reward.dist;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import pers.xanadu.enderdragon.manager.GlowManager;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.util.MyDragon;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static pers.xanadu.enderdragon.manager.GlowManager.setGlowingColor;

public class Dist_Drop extends RewardDist {
    private final GlowManager.GlowColor color;
    public Dist_Drop(ConfigurationSection section){
        if(section == null) color = GlowManager.GlowColor.NONE;
        else {
            String color = section.getString("glow","NONE").toUpperCase();
            this.color = GlowManager.GlowColor.valueOf(color);
        }
    }
    public Dist_Drop(){
        color = GlowManager.GlowColor.NONE;
    }
    @Override
    public void handle_dist(final MyDragon myDragon, final EnderDragon dragon, final Player killer){
        List<ItemStack> items = getDropReward(myDragon);
        if(items.isEmpty()) return;
        World world = dragon.getWorld();
        Location loc = dragon.getLocation();
        items.forEach(item->{
            Item entity_item = world.dropItemNaturally(loc,item);
            ChatColor color = getGlowColor();
            if(color!=null) setGlowingColor(entity_item,color);
            double x = ThreadLocalRandom.current().nextDouble(0.25);
            double y = ThreadLocalRandom.current().nextDouble(0.25);
            double z = ThreadLocalRandom.current().nextDouble(0.25);
            entity_item.setVelocity(new Vector(x,y,z));
        });
    }

    public ChatColor getGlowColor(){
        return GlowManager.getGlowColor(color);
    }
}
