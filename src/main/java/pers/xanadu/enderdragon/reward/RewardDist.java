package pers.xanadu.enderdragon.reward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.reward.dist.*;
import pers.xanadu.enderdragon.util.MyDragon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RewardDist {
    public static RewardDist parse(ConfigurationSection section){
        if(section == null) return new Dist_Drop();
        String type_name = section.getString("type");
        DistType type = DistType.getByName(type_name);
        switch (type){
            case all : {
                return new Dist_All();
            }
            case drop : {
                return new Dist_Drop(section.getConfigurationSection("drop"));
            }
            case killer : {
                return new Dist_Killer();
            }
            case pack : {
                return new Dist_Pack(section.getConfigurationSection("pack"));
            }
            case rank : {
                return new Dist_Rank(section.getConfigurationSection("rank"));
            }
            case termwise : {
                return new Dist_Termwise();
            }
            default : {
                Lang.error("Unknown reward_dist type: "+type_name);
                return new Dist_Drop();
            }
        }
    }
    public List<ItemStack> getDropReward(MyDragon myDragon){
        List<ItemStack> list = new ArrayList<>();
        for(Reward reward : myDragon.datum){
            double chance = reward.getChance().getValue();
            if(chance > ThreadLocalRandom.current().nextDouble(100)){
                list.add(reward.getItem());
            }
        }
        return list;
    }
    public void handle_dist(final MyDragon myDragon,final EnderDragon dragon,final Player killer){

    }

}
