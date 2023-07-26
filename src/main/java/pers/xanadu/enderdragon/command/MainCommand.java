package pers.xanadu.enderdragon.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.GuiManager;
import pers.xanadu.enderdragon.manager.RewardManager;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.manager.TimerManager;
import pers.xanadu.enderdragon.reward.Chance;
import pers.xanadu.enderdragon.config.FileUpdater;
import pers.xanadu.enderdragon.task.DragonRespawnTimer;
import pers.xanadu.enderdragon.util.MyDragon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class MainCommand implements CommandExecutor {
    private Class<?> CraftItemStackClass;
    private Class<?> NBTTagCompoundClass;
    private Class<?> ItemStackClass_e;
    private Method asNMSCopy;
    private Method save;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendCommandTips(sender);
            return false;
        }
        switch(args[0].toLowerCase()){
//            case "parse" : {
//                if(!(sender instanceof Player)) {
//                    Lang.sendFeedback(sender,Lang.command_only_player);
//                    return false;
//                }
//                Player p = (Player) sender;
//                if(sender.isOp()){
////                        List<Entity> entities = p.getNearbyEntities(50,50,50);
////                        entities.forEach(entity -> {
////                            if(entity instanceof org.bukkit.entity.EnderDragon){
////                                ScoreboardManager sm = server.getScoreboardManager();
////                                CraftScoreboardManager csm = (CraftScoreboardManager) sm;
////                                try {
////                                    Field field = csm.getClass().getDeclaredField("scoreboards");
////                                    field.setAccessible(true);
////                                    Collection<CraftScoreboard> sbs = (Collection<CraftScoreboard>) field.get(csm);
////                                    sbs.forEach(sb->{
////                                        Team team = sb.getEntryTeam(entity.getUniqueId().toString());
////                                        if(team!=null) Bukkit.broadcastMessage(team.getName());
////                                    });
////                                } catch (ReflectiveOperationException e) {
////                                    throw new RuntimeException(e);
////                                }
////                            }
//////                            if(entity instanceof org.bukkit.entity.EnderDragon){
//////                                Bukkit.broadcastMessage(entity.getName());//1 Special Ender Dragon
//////                            }
//////                            if(entity instanceof ComplexEntityPart){
//////                                Bukkit.broadcastMessage(entity.getName());//1 Special Ender Dragon
//////                            }
//////                            if(entity instanceof ComplexLivingEntity){
//////                                Bukkit.broadcastMessage(entity.getName());//8 EnderDragon
//////                            }
////                        });
//                }
//                return true;
//            }
            case "reload" : {
                if(!sender.hasPermission("ed.reload")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length == 1){
                    closeAllInventory();
                    reloadAll();
                    Lang.sendFeedback(sender,Lang.command_reload_config);
                    return true;
                }
                break;
            }
            case "update" : {
                if(!sender.hasPermission("ed.update")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length == 1){
                    try {
                        FileUpdater.update();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
                break;
            }
            case "respawn" : {//ed respawn [world_name]
                if(!sender.hasPermission("ed.respawn")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length == 1){
                    if(!(sender instanceof Player)){
                        Lang.sendFeedback(sender,"§cConsole usage: /ed respawn <world_name>");
                        return false;
                    }
                    Player player = (Player) sender;
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(player);
                    return true;
                }
                else if(args.length == 2){
                    String world_name = args[1];
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(sender,world_name);
                    return true;
                }
                else {
                    int size = args.length;
                    StringBuilder builder = new StringBuilder(args[1]);
                    for(int i=2;i<size;i++) builder.append(" ").append(args[i]);
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(sender,builder.toString());
                    return true;
                }
            }
            case "respawn_cd" : {
                if(!sender.hasPermission("ed.respawn")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(!Config.respawn_cd_enable){
                    Lang.sendFeedback(sender,Lang.command_respawn_cd_disable);
                    return false;
                }
                //ed respawn_cd get <world_name>
                if(args.length == 3 && args[1].equalsIgnoreCase("get")){
                    String world_name = args[2];
                    DragonRespawnTimer timer = TimerManager.getTimer(world_name);
                    String value = timer!=null?timer.toString():world_name+": null";
                    Lang.sendFeedback(sender,value);
                    return true;
                }
                //ed respawn_cd set <world_name> <delay:second>
                else if(args.length == 4 && args[1].equalsIgnoreCase("set")){
                    String world_name = args[2];
                    World world = Bukkit.getWorld(world_name);
                    if(world == null){
                        Lang.sendFeedback(sender,"§c"+DragonManager.DragonRespawnResult.world_not_found.getMessage());
                        return false;
                    }
                    if(world.getEnvironment() != World.Environment.THE_END){
                        Lang.sendFeedback(sender,"§c"+DragonManager.DragonRespawnResult.world_wrong_env.getMessage());
                        return false;
                    }
                    int delay = Integer.parseInt(args[3]);
                    TimerManager.setTimer(world_name,new DragonRespawnTimer(world_name,delay));
                    TimerManager.save();
                    Lang.sendFeedback(sender,Lang.command_respawn_cd_set.replaceAll("\\{second}",args[3]));
                    return true;
                }
                //ed respawn_cd removeAll
                else if(args.length == 2 && args[1].equalsIgnoreCase("removeAll")){
                    TimerManager.removeAll();
                    TimerManager.save();
                    Lang.sendFeedback(sender,Lang.command_respawn_cd_removeAll);
                    return true;
                }
                //ed respawn_cd remove <world_name>
                else if(args.length == 3 && args[1].equalsIgnoreCase("remove")){
                    String world_name = args[2];
                    TimerManager.removeTimer(world_name);
                    TimerManager.save();
                    Lang.sendFeedback(sender,Lang.command_respawn_cd_remove.replaceAll("\\{world}",world_name));
                    return true;
                }
                //ed respawn_cd start <world_name>
                else if(args.length == 3 && args[1].equalsIgnoreCase("start")){
                    String world_name = args[2];
                    DragonRespawnTimer timer = TimerManager.getTimer(world_name);
                    if(timer == null){
                        Lang.sendFeedback(sender,Lang.command_respawn_cd_start_none);
                        return false;
                    }
                    if(timer.isRunning()){
                        Lang.sendFeedback(sender,Lang.command_respawn_cd_start_already_started);
                        return false;
                    }
                    TimerManager.startTimer(world_name);
                    Lang.sendFeedback(sender,Lang.command_respawn_cd_start_succeed);
                    return true;
                }
                break;
            }
            case "drop" : {
                if(!(sender instanceof Player)) {
                    Lang.sendFeedback(sender,Lang.command_only_player);
                    return false;
                }
                Player p = (Player) sender;
                switch (args[1].toLowerCase()){
                    case "gui" : {
                        if(!sender.hasPermission("ed.drop.gui")){
                            Lang.sendFeedback(sender,Lang.command_no_permission);
                            return false;
                        }
                        if(args.length == 2){
                            GuiManager.openGui(p, Config.main_gui,false);
                            return true;
                        }
                        if(args.length == 3){
                            String key = args[2];
                            MyDragon dragon = DragonManager.mp.get(key);
                            if(dragon == null){
                                Lang.sendFeedback(sender, Lang.dragon_not_found);
                                return false;
                            }
                            GuiManager.openGui(p,dragon.drop_gui,key,false);
                            return true;
                        }
                        break;
                    }
                    case "clear" : {
                        if(!sender.hasPermission("ed.drop.edit")){
                            Lang.sendFeedback(sender,Lang.command_no_permission);
                            return false;
                        }
                        if(args.length == 3) {
                            String key = args[2];
                            RewardManager.clearItem(key);
                            Lang.sendFeedback(sender,Lang.command_drop_item_clear);
                            return true;
                        }
                        break;
                    }
                    case "add" : {
                        if(!sender.hasPermission("ed.drop.edit")){
                            Lang.sendFeedback(sender,Lang.command_no_permission);
                            return false;
                        }
                        if(args.length == 4) {
                            if(p.getInventory().getItemInMainHand().getType() == Material.AIR){
                                Lang.sendFeedback(p, Lang.command_drop_item_add_empty);
                                return false;
                            }
                            String ChanceStr = args[3];
                            String key = args[2];
                            double chance = -1;
                            try {
                                chance = Double.parseDouble(ChanceStr);
                            } catch (NumberFormatException ex){
                                Lang.sendFeedback(p,Lang.command_drop_item_add_invalid_chance + ChanceStr);
                                return false;
                            }
                            if(chance <= 0) {
                                Lang.sendFeedback(p,Lang.command_drop_item_add_invalid_chance + ChanceStr);
                                return false;
                            }
                            if(chance > 100) chance = 100;
                            ItemStack item = p.getItemInHand().clone();
                            RewardManager.addItem(key,item,new Chance(chance,ChanceStr));
                            Lang.sendFeedback(p,Lang.command_drop_item_add_succeed.replaceAll("\\{chance}",ChanceStr));
                            return true;
                        }
                        break;
                    }
                    case "edit" : {
                        if(!sender.hasPermission("ed.drop.edit")){
                            Lang.sendFeedback(sender,Lang.command_no_permission);
                            return false;
                        }
                        if(args.length == 2){
                            GuiManager.openGui(p, Config.main_gui,true);
                            return true;
                        }
                        break;
                    }
                    case "remove" : {
                        if(!sender.hasPermission("ed.drop.edit")){
                            Lang.sendFeedback(sender,Lang.command_no_permission);
                            return false;
                        }
                        if(args.length == 4) {
                            try{
                                int idx = Integer.parseInt(args[3]);
                                boolean b = RewardManager.removeItem(args[2],idx);
                                if(b) Lang.sendFeedback(sender,Lang.command_drop_item_remove_succeed);
                                else Lang.sendFeedback(sender,Lang.command_drop_item_remove_fail);
                                return true;
                            }catch (NumberFormatException e){
                                Lang.sendFeedback(sender,Lang.command_drop_item_remove_invalid_num+args[3]);
                                return false;
                            }
                        }
                        break;
                    }
                    default : break;
                }
            }
        }
        sendCommandTips(sender);
        return false;
    }
    private static void sendCommandTips(CommandSender sender){
        sender.sendMessage(Lang.CommandTips1);
        sender.sendMessage(Lang.CommandTips2);
        sender.sendMessage(Lang.CommandTips3);
        sender.sendMessage(Lang.CommandTips4);
    }

}


//                        long st=System.currentTimeMillis(),ed;
//                        for(int i=0;i<1;i++) {
//                            try {
//                                ItemStack item = p.getItemInHand();
//                                Class<?> CraftItemStackClass = Class.forName("org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack");
//                                Object ci = CraftItemStackClass.cast(item);
//                                Method asNMSCopy = CraftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
//                                Object ei = asNMSCopy.invoke(ci, item);
//                                Class<?> NBTTagCompoundClass = Class.forName("net.minecraft.server.v1_13_R1.NBTTagCompound");
//                                Method save = ei.getClass().getDeclaredMethod("save", NBTTagCompoundClass);
//                                Object cpd = save.invoke(ei, NBTTagCompoundClass.newInstance());
//                                String str = cpd.toString();
//                                p.sendMessage(cpd.toString());
//                            } catch (ReflectiveOperationException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        ed=System.currentTimeMillis();
//                        Bukkit.broadcastMessage("test #1: "+(ed-st)+"ms");
//                        st=System.currentTimeMillis();
//                        for(int i=0;i<1e5;i++) {
//                            ItemStack item = p.getItemInHand();
//                            net.minecraft.server.v1_13_R1.ItemStack ni = CraftItemStack.asNMSCopy(item);
//                            NBTTagCompound cpd = ni.save(new NBTTagCompound());
//                            String str = cpd.toString();
//                        }
//                        ed=System.currentTimeMillis();
//                        Bukkit.broadcastMessage("test #2: "+(ed-st)+"ms");
//
//                        try {
//                            CraftItemStackClass=Class.forName("org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack");
//                            asNMSCopy = CraftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
//                            NBTTagCompoundClass = Class.forName("net.minecraft.server.v1_13_R1.NBTTagCompound");
//                            ItemStackClass_e = Class.forName("net.minecraft.server.v1_13_R1.ItemStack");
//                            save = ItemStackClass_e.getDeclaredMethod("save", NBTTagCompoundClass);
//                        } catch (ReflectiveOperationException e) {
//                            e.printStackTrace();
//                        }
//                        for(int i=0;i<1e5;i++) {
//                            try {
//                                ItemStack item = p.getItemInHand();
//                                Object ci = CraftItemStackClass.cast(item);
//                                Object ei = asNMSCopy.invoke(ci, item);
//                                Object cpd = save.invoke(ei, NBTTagCompoundClass.newInstance());
//                                String str = cpd.toString();
//                                //p.sendMessage(cpd.toString());
//                            } catch (ReflectiveOperationException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        ed=System.currentTimeMillis();
//                        Bukkit.broadcastMessage("test #3: "+(ed-st)+"ms");


//                        st=System.currentTimeMillis();
//
//                        for(int i=0;i<1;i++) {
//                            ItemStack item = p.getItemInHand();
//                            String str = new NbtItemStack(item).getOrCreateTag().toString();
//                            //Bukkit.broadcastMessage(str);
//                            p.sendMessage(str);
//                        }
//                        ed=System.currentTimeMillis();
//                        Bukkit.broadcastMessage("test #3: "+(ed-st)+"ms");

//        net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        NBTTagCompound cpd = nmsItem.save(new NBTTagCompound());

//                        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//                        NBTTagCompound cpd = nmsItem.b(new NBTTagCompound());
//                        p.sendMessage(cpd.toString());