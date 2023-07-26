package pers.xanadu.enderdragon.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DamageManager;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.TimerManager;
import pers.xanadu.enderdragon.util.MyDragon;
import pers.xanadu.enderdragon.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class DragonDeathListener implements Listener {
    @EventHandler
    public void OnDragonDeath(final EntityDeathEvent e){
        if(!(e.getEntity() instanceof EnderDragon)) return;
        EnderDragon dragon = (EnderDragon) e.getEntity();
        String unique_name = DragonManager.getSpecialKey(dragon);
        if(unique_name == null) return;
        MyDragon myDragon = DragonManager.mp.get(unique_name);
        if(myDragon == null) return;
        int times = data.getInt("times");
        data.set("times",times+1);
        try{
            data.save(dataF);
        }catch (IOException ex){
            Lang.error(Lang.plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
        }
        e.setDroppedExp(myDragon.exp_drop);
        if(myDragon.dragon_egg_spawn_chance > ThreadLocalRandom.current().nextDouble(100)){
            new BukkitRunnable() {
                @Override
                public void run() {
                    Block block = e.getEntity().getWorld().getBlockAt(myDragon.dragon_egg_spawn_x, myDragon.dragon_egg_spawn_y, myDragon.dragon_egg_spawn_z);
                    block.setType(Material.DRAGON_EGG);
                }
            }.runTaskLater(plugin, myDragon.dragon_egg_spawn_delay);
        }
        Player p = e.getEntity().getKiller();
        myDragon.reward_dist.handle_dist(myDragon,dragon,p);
        {
            List<String> processed = new ArrayList<>();
            if(p != null){
                for(String str : myDragon.death_broadcast_msg){
                    processed.add(str.replaceAll("%times%",String.valueOf(times)).replaceAll("%player%",p.getDisplayName()));
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
                if(name.equals("")) name = Lang.dragon_no_killer;
                if(name.endsWith(",")) name = name.substring(0,name.length()-1);
                for(String str : myDragon.death_broadcast_msg){
                    processed.add(str.replaceAll("%times%",String.valueOf(times)).replaceAll("%player%", name));
                }
            }
            handleBroadcast(processed,myDragon,dragon);
        }
        DamageManager.data.remove(dragon.getUniqueId());
        Lang.runCommands(myDragon.death_cmd,p);
        if(p != null){
            for(String str : myDragon.msg_to_killer){
                Lang.sendFeedback(p,str.replaceAll("%times%",String.valueOf(times)));
            }
        }
        TimerManager.startTimer(dragon.getWorld().getName());
    }
    public static void handleBroadcast(final List<String> list,final MyDragon myDragon,final EnderDragon dragon){
        boolean find = false;
        for(String str : list){
            if(str.contains("{damage_statistics}")){
                find = true;
                break;
            }
        }
        if(!find) Lang.broadcastMSG(list);
        else{
            final TextComponent hover = getHover(myDragon,dragon);
            for(String str : list){
                final String[] splits = str.split("\\{damage_statistics}");
                final ComponentBuilder builder = new ComponentBuilder(Lang.plugin_prefix);
                int size = splits.length;
                for(int i=0;i<size-1;i++){
                    builder.append(splits[i]).append(hover);
                }
                builder.append(splits[size-1]);
                if(str.endsWith("{damage_statistics}")) builder.append(hover);
                Bukkit.getOnlinePlayers().forEach(p->p.spigot().sendMessage(builder.create()));
            }
        }
    }
    public static TextComponent getHover(final MyDragon myDragon,final EnderDragon dragon) {
        final TextComponent text = new TextComponent(Lang.dragon_damage_statistics_text);
        List<Pair<String,Double>> list = DamageManager.getDamageList(dragon.getUniqueId());
        list.sort(DamageManager::sortByDamage);
        double sum = 0d;
        for(Pair<String,Double> pair : list){
            sum += pair.second;
        }
        StringBuilder builder = new StringBuilder();
        for(String raw : Lang.dragon_damage_statistics_hover_prefix){
            builder.append(raw
                    .replaceAll("%dragon_display_name%",myDragon.display_name)
                    .replaceAll("%damage_sum%",String.format("%.2f",sum))
            ).append("\n");
        }
        int i = 0;
        for(Pair<String,Double> pair : list){
            String raw = Lang.dragon_damage_statistics_hover_mt;
            if(i < Config.damage_statistics_limit){
                double damage = pair.second;
                double percent = damage/sum*100;
                builder.append(raw
                        .replaceAll("%rank%", String.valueOf(++i))
                        .replaceAll("%player%", pair.first)
                        .replaceAll("%damage%",String.format("%.2f",damage))
                        .replaceAll("%percent%",String.format("%.2f%%",percent))
                ).append("\n");
            }
        }
        if(list.size()-i>0){
            String raw = Lang.dragon_damage_statistics_hover_exceeds_limit;
            builder.append(raw
                    .replaceAll("%exceeds_number%", String.valueOf(list.size()-i))
            ).append("\n");
        }
        for(String raw : Lang.dragon_damage_statistics_hover_suffix){
            builder.append(raw
                    .replaceAll("%dragon_display_name%",myDragon.display_name)
                    .replaceAll("%damage_sum%",String.format("%.2f",sum))
            );
        }
        BaseComponent[] cmp = new ComponentBuilder(builder.toString()).create();
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,cmp));
        return text;
    }

}
