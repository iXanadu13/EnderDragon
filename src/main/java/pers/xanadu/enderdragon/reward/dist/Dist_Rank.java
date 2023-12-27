package pers.xanadu.enderdragon.reward.dist;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.reward.RewardDist;
import pers.xanadu.enderdragon.metadata.MyDragon;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Dist_Rank extends RewardDist {
    private final int max_num;
    public Dist_Rank(ConfigurationSection section){
        if(section == null) max_num = 1;
        else max_num = section.getInt("max_num",1);
    }
    public int getMax_num(){
        return max_num;
    }
    @Override
    public void handle_dist(final MyDragon myDragon,final EnderDragon dragon,final Player killer){
        ConcurrentHashMap<String,Double> damage_data = DamageManager.data.get(dragon.getUniqueId());
        if(damage_data == null) return;
        List<ItemStack> items = getDropReward(myDragon);
        if(items.isEmpty()) return;
        int m = Math.min(max_num,damage_data.size());
        if(m<=0) return;
        List<Map.Entry<String, Double>> list = damage_data.entrySet().stream().sorted(Dist_Rank::sortByDamage).collect(Collectors.toList());
        for(int i=0;i<m;i++){
            String name = list.get(i).getKey();
            Player p = Bukkit.getPlayer(name);
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
    private static <T> int sortByDamage(Map.Entry<T, Double> e1,Map.Entry<T, Double> e2){
        if(e2.getValue()>e1.getValue()) return 1;
        if(e2.getValue().equals(e1.getValue())) return 0;
        return -1;
    }
}
