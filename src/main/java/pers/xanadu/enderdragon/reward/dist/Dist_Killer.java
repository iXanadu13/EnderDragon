package pers.xanadu.enderdragon.reward.dist;

import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.util.MyDragon;

import java.util.List;

public class Dist_Killer extends RewardDist {
    @Override
    public void handle_dist(final MyDragon myDragon,final EnderDragon dragon,final Player p){
        List<ItemStack> items = getDropReward(myDragon);
        if(items.isEmpty()) return;
        if(p != null){
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
    }
}
