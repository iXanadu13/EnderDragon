package pers.xanadu.enderdragon.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.*;
import pers.xanadu.enderdragon.maven.DependencyManager;
import pers.xanadu.enderdragon.reward.Chance;
import pers.xanadu.enderdragon.config.FileUpdater;
import pers.xanadu.enderdragon.task.DragonRespawnTimer;
import pers.xanadu.enderdragon.metadata.MyDragon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendCommandTips(sender);
            return false;
        }
        switch(args[0].toLowerCase()){
            case "parse" : {
                if(!sender.isOp()) return false;
                if(args.length == 2) GroovyManager.invoke("test.groovy", args[1]);
                else if(args.length == 3) GroovyManager.invoke("test.groovy",args[1],args[2]);
                return true;
            }
//            case "worlds" : {
//                if(!sender.isOp()){
//                    Lang.sendFeedback(sender,Lang.command_no_permission);
//                    return false;
//                }
//                if(args.length < 2) break;
//                switch (args[1].toLowerCase()){
//                    case "list" : {
//
//                    }
//                }
//                break;
//            }
            case "action" : {
                if(!sender.isOp()){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length < 4) break;
                Player p = Bukkit.getPlayerExact(args[1]);
                if(p==null && "me".equals(args[1]) || "!me".equals(args[1])){
                    if(sender instanceof Player) p = (Player) sender;
                    else p = null;
                }
                if(p == null) return false;
                ActionManager.executeAction(p,args);
                return true;
            }
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
                if(args.length == 2){
                    if(args[1].equalsIgnoreCase("script")){
                        if(DependencyManager.isGroovyLoaded()) GroovyManager.reload();
                        else Lang.sendFeedback(sender,Lang.expansion_groovy_disable);
                        return true;
                    }
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
            case "spawn" : {//ed spawn [dragon] (world_name)
                if(!sender.hasPermission("ed.respawn")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length == 2){
                    if(!(sender instanceof Player)){
                        Lang.sendFeedback(sender,"§cConsole usage: /ed spawn [dragon] [world_name]");
                        return false;
                    }
                    Player player = (Player) sender;
                    DragonManager.initiateRespawn(player,args[1]);
                    return true;
                }
                else if(args.length == 3){
                    String world_name = args[2];
                    DragonManager.initiateRespawn(sender,world_name,args[1]);
                    return true;
                }
                else if(args.length > 3){//if world_name contains " "
                    int size = args.length;
                    StringBuilder builder = new StringBuilder(args[2]);
                    for(int i=3;i<size;i++) builder.append(" ").append(args[i]);
                    DragonManager.initiateRespawn(sender,builder.toString(),args[1]);
                    return true;
                }
                //unreachable
                break;
            }
            case "respawn" : {//ed respawn (world_name)
                if(!sender.hasPermission("ed.respawn")){
                    Lang.sendFeedback(sender,Lang.command_no_permission);
                    return false;
                }
                if(args.length == 1){
                    if(!(sender instanceof Player)){
                        Lang.sendFeedback(sender,"§cConsole usage: /ed respawn [world_name]");
                        return false;
                    }
                    Player player = (Player) sender;
                    DragonManager.initiateRespawn(player);
                    return true;
                }
                else if(args.length == 2){
                    String world_name = args[1];
                    DragonManager.initiateRespawn(sender,world_name);
                    return true;
                }
                else {//if world_name contains " "
                    int size = args.length;
                    StringBuilder builder = new StringBuilder(args[1]);
                    for(int i=2;i<size;i++) builder.append(" ").append(args[i]);
                    DragonManager.initiateRespawn(sender,builder.toString());
                    return true;
                }
                //unreachable
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
                if(args.length < 2) break;
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
                            MyDragon dragon = DragonManager.get_dragon_config(key);
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


