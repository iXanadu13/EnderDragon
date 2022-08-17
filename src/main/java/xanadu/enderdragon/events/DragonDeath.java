package xanadu.enderdragon.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import xanadu.enderdragon.EnderDragon;
import xanadu.enderdragon.lang.Message;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static xanadu.enderdragon.EnderDragon.*;

public class DragonDeath implements Listener {
    @EventHandler
    public void OnDragonDeath(EntityDeathEvent e) throws IOException {
        if(e.getEntity().getType() != EntityType.ENDER_DRAGON){return;}
        int times = data.getInt("times");
        data.set("times",times+1);
        data.save(dataF);
        Player p = e.getEntity().getKiller();
        int exp = plugin.getConfig().getInt("special-dragon.exp-drop");
        int x = plugin.getConfig().getInt("special-dragon.dragon-egg-spawn.x");
        int y = plugin.getConfig().getInt("special-dragon.dragon-egg-spawn.y");
        int z = plugin.getConfig().getInt("special-dragon.dragon-egg-spawn.z");
        long delay = plugin.getConfig().getInt("special-dragon.dragon-egg-spawn.delay");
        boolean UseCMD = plugin.getConfig().getBoolean("command.enable");
        boolean EggChance = plugin.getConfig().getInt("special-dragon.dragon-egg-spawn.chance") > ThreadLocalRandom.current().nextInt(0, 100);
        boolean AllBroadcast = !plugin.getConfig().getBoolean("only-special-death-remind");
        String skill = Message.KillerMessage;
        String KillMsg = Message.DragonKillingBroadcast;
        String nobody = Message.NobodyKill;
        String InvIsFull = Message.PlayerInvFull;
        if(KillMsg != null) {
            if(p != null) {
                if (isSpecial(e.getEntity()) || AllBroadcast) {
                    Bukkit.broadcastMessage(prefix + KillMsg.replaceAll("%times%", String.valueOf(times)).replaceAll("%player%", p.getDisplayName()));
                }
            }
            else {
                StringBuilder names = new StringBuilder();
                for (Entity entity : e.getEntity().getNearbyEntities(5,5,5)) {
                    if (entity instanceof Player) {
                        names.append(((Player) entity).getDisplayName()).append(",");
                    }
                }
                String name = names.toString();
                if(name.equals("")){name = nobody;}
                if(name.endsWith(",")){name = name.substring(0,name.length()-1);}
                if (isSpecial(e.getEntity()) || AllBroadcast) {
                    Bukkit.broadcastMessage(prefix + KillMsg.replaceAll("%times%", String.valueOf(times)).replaceAll("%player%", name));
                }
            }
        }
        if(UseCMD) {
            if (isSpecial(e.getEntity())) {
                List<String> CMDList = plugin.getConfig().getStringList("command.special-dragon");
                for (String command : CMDList) {
                    if (!(p == null && command.contains("%player%"))) {
                        server.dispatchCommand(server.getConsoleSender(), command.replaceAll("%player%", p.getName()));
                    }
                }
            }
            else {
                List<String> CMDList = plugin.getConfig().getStringList("command.normal-dragon");
                for (String command : CMDList) {
                    if (!(p == null && command.contains("%player%"))) {
                        server.dispatchCommand(server.getConsoleSender(), command.replaceAll("%player%", p.getName()));
                    }
                }
            }
        }
        if(isSpecial(e.getEntity())){
            e.setDroppedExp(exp);
            if (EggChance) {
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Block block = e.getEntity().getWorld().getBlockAt(x, y, z);
                        block.setType(Material.DRAGON_EGG);
                    }
                };
                runnable.runTaskLater(EnderDragon.getPlugin(EnderDragon.class), delay );
            }
            if(p != null){p.sendMessage(prefix + skill);}

            List<String> datum = data.getStringList("items");
            int max = datum.size() / 2 ;
            boolean warning = false;
            for(int i=0 ; i<max ; ){
                i = i + 1;
                double chance = Double.parseDouble(datum.get(i*2-1));
                boolean judge = chance > Math.random() * 100;
                if(judge){
                    String str = datum.get(i*2-2);
                    ItemStack item = StringToItemStack(str);
                    if(p == null){
                        e.getEntity().getWorld().dropItem(e.getEntity().getLocation(),item);
                    }
                    else{
                        if (p.getInventory().firstEmpty() == -1) {
                            warning = true;
                        }
                        PlayerInventory inv = p.getInventory();
                        inv.addItem(item);
                    }
                }
            }
            if(warning){p.sendMessage(prefix + InvIsFull);}
            if(mcMainVersion < 11) {
                List<String> special = data.getStringList("special");
                special.remove(e.getEntity().getUniqueId().toString());
                data.set("special", special);
                data.save(dataF);
            }
        }
    }
}
