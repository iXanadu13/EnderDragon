package pers.xanadu.enderdragon.reward.dist;

import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.util.AliasSample;
import pers.xanadu.enderdragon.metadata.MyDragon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Dist_Termwise extends RewardDist {
    @Override
    public void handle_dist(final MyDragon myDragon,final EnderDragon dragon,final Player killer){
        ConcurrentHashMap<String,Double> damage_data = DamageManager.data.get(dragon.getUniqueId());
        if(damage_data == null) return;
        List<ItemStack> items = getDropReward(myDragon);
        if(items.isEmpty()) return;
        AliasSample alias = new AliasSample(new ArrayList<>(damage_data.values()));
        List<String> list = new ArrayList<>(damage_data.keySet());
        items.forEach(item->{
            int idx = alias.next();
            String receiver = list.get(idx);
            Player p = Bukkit.getPlayer(receiver);
            if(p != null){
                if(p.getInventory().firstEmpty() == -1){
                    p.getWorld().dropItem(p.getLocation(),item);
                    Lang.sendFeedback(p, Lang.dragon_player_inv_full);
                }
                else p.getInventory().addItem(item);
            }
        });
    }
}
