package pers.xanadu.enderdragon.reward.dist;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.util.MyDragon;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Dist_All extends RewardDist {
    @Override
    public void handle_dist(final MyDragon myDragon,final EnderDragon dragon,final Player killer){
        ConcurrentHashMap<String,Double> damage_data = DamageManager.data.get(dragon.getUniqueId());
        if(damage_data == null) return;
        List<ItemStack> items = getDropReward(myDragon);
        if(items.isEmpty()) return;
        Bukkit.getOnlinePlayers().forEach(p->{
            if(damage_data.containsKey(p.getName())){
                boolean full = false;
                for(ItemStack item : items){
                    if(full || p.getInventory().firstEmpty() == -1){
                        p.getWorld().dropItem(p.getLocation(),item);
                        full = true;
                    }
                    else p.getInventory().addItem(item);
                }
                if(full) Lang.sendFeedback(p, Lang.dragon_player_inv_full);
            }
        });
    }
}