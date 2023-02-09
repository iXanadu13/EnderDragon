package xanadu.enderdragon.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xanadu.enderdragon.gui.Reward;
import xanadu.enderdragon.utils.MyDragon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static xanadu.enderdragon.EnderDragon.*;
import static xanadu.enderdragon.config.Lang.*;
import static xanadu.enderdragon.manager.DragonManager.getSpecialKey;
import static xanadu.enderdragon.manager.DragonManager.mp;

public class DragonDeathListener implements Listener {
    @EventHandler
    public void OnDragonDeath(EntityDeathEvent e){
        if(!(e.getEntity() instanceof EnderDragon dragon)) return;
        String unique_name = getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = mp.get(unique_name);
        if(myDragon == null) return;
        int times = data.getInt("times");
        data.set("times",times+1);
        try{
            data.save(dataF);
        }catch (IOException ex){
            error(plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
        }
        e.setDroppedExp(myDragon.exp_drop);
        if(myDragon.dragon_egg_spawn_chance > ThreadLocalRandom.current().nextDouble(100)){
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    Block block = e.getEntity().getWorld().getBlockAt(myDragon.dragon_egg_spawn_x, myDragon.dragon_egg_spawn_y, myDragon.dragon_egg_spawn_z);
                    block.setType(Material.DRAGON_EGG);
                }
            };
            runnable.runTaskLater(plugin, myDragon.dragon_egg_spawn_delay);
        }
        Player p = e.getEntity().getKiller();
        List<ItemStack> list = new ArrayList<>();
        for(Reward reward : myDragon.datum){
            double chance = reward.getChance().getValue();
            if(chance > ThreadLocalRandom.current().nextDouble(100)){
                list.add(reward.getItem());
            }
        }
        if(!list.isEmpty()){
            boolean warn = false;
            if(p == null){
                Location loc = dragon.getLocation();
                World world = dragon.getWorld();
                list.forEach(item -> world.dropItem(loc,item));
            }
            else{
                for(ItemStack item : list){
                    if(p.getInventory().firstEmpty() == -1){
                        p.getWorld().dropItem(p.getLocation(),item);
                        warn = true;
                    }
                    else p.getInventory().addItem(item);
                }
            }
            if(warn) sendFeedback(p, dragon_player_inv_full);
        }
        runCommands(myDragon.death_cmd,p);
        if(p != null){
            for(String str : myDragon.msg_to_killer){
                sendFeedback(p,str);
            }
            for(String str : myDragon.death_broadcast_msg){
                broadcastMSG(str.replaceAll("%times%",String.valueOf(times)).replaceAll("%player%",p.getDisplayName()));
            }
        }
        else{
            StringBuilder names = new StringBuilder();
            List<Entity> entities = dragon.getNearbyEntities(5,5,5);
            for (Entity entity : entities) {
                if (entity instanceof Player) {
                    names.append(((Player) entity).getDisplayName()).append(",");
                }
            }
            String name = names.toString();
            if(name.equals("")) name = dragon_no_killer;
            if(name.endsWith(",")) name = name.substring(0,name.length()-1);
            for(String str : myDragon.death_broadcast_msg){
                broadcastMSG(str.replaceAll("%times%",String.valueOf(times)).replaceAll("%player%", name));
            }
        }
    }
}
