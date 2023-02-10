package xanadu.enderdragon.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xanadu.enderdragon.EnderDragon;
import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.utils.Chance;
import xanadu.enderdragon.utils.FileUpdater;
import xanadu.enderdragon.utils.MyDragon;

import java.io.IOException;

import static xanadu.enderdragon.EnderDragon.*;
import static xanadu.enderdragon.config.Lang.*;
import static xanadu.enderdragon.manager.DragonManager.*;
import static xanadu.enderdragon.manager.GuiManager.openGui;
import static xanadu.enderdragon.manager.RewardManager.addItem;
import static xanadu.enderdragon.manager.RewardManager.clearItem;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendCommandTips(sender);
            return false;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()){
                case "reload" : {
                    if(!sender.hasPermission("ed.reload")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    closeAllInventory();
                    reloadAll();
                    sendFeedback(sender,Lang.command_reload_config);
                    return true;
                }
                case "respawn" : {
                    if(!sender.hasPermission("ed.respawn")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    if(!(sender instanceof Player)){
                        sendFeedback(sender,Lang.command_only_player);
                        return false;
                    }
                    Player player = (Player) sender;
                    EnderDragon.getInstance().getDragonManager().initiateRespawn(player);
                    return true;
                }
                case "update" : {
                    if(!sender.hasPermission("ed.update")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    try {
                        FileUpdater.update();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
                default : {
                    sendCommandTips(sender);
                    return false;
                }
            }
        }
        if (args[0].equalsIgnoreCase("drop")) {
            if(!(sender instanceof Player)) {
                sendFeedback(sender,Lang.command_only_player);
                return false;
            }
            Player p = (Player) sender;
            switch (args[1].toLowerCase()){
                case "gui" : {
                    if(!sender.hasPermission("ed.drop.gui")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    if(args.length == 2){
                        openGui(p, Config.main_gui);
                        return true;
                    }
                    if(args.length == 3){
                        String key = args[2];
                        MyDragon dragon = mp.get(key);
                        if(dragon == null){
                            sendFeedback(sender,dragon_not_found);
                            return false;
                        }
                        openGui(p,dragon.drop_gui,key);
                        return true;
                    }
                    break;
                }
                case "clear" : {
                    if(!sender.hasPermission("ed.drop.edit")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    if(args.length == 3) {
                        String key = args[2];
                        clearItem(key);
                        sendFeedback(sender,Lang.command_drop_item_clear);
                        return true;
                    }
                    break;
                }
                case "add" : {
                    if(!sender.hasPermission("ed.drop.edit")){
                        sendFeedback(sender,Lang.command_no_permission);
                        return false;
                    }
                    if(args.length == 4) {
                        if(p.getInventory().getItemInMainHand().getType() == Material.AIR){
                            sendFeedback(p, command_drop_item_add_empty);
                            return false;
                        }
                        String ChanceStr = args[3];
                        String key = args[2];
                        double chance = -1;
                        try {
                            chance = Double.parseDouble(ChanceStr);
                        } catch (NumberFormatException ex){
                            sendFeedback(p,Lang.command_drop_item_add_invalid_chance + ChanceStr);
                            return false;
                        }
                        if(chance <= 0) {
                            sendFeedback(p,Lang.command_drop_item_add_invalid_chance + ChanceStr);
                            return false;
                        }
                        if(chance > 100) chance = 100;
                        ItemStack item = p.getItemInHand();
                        addItem(key,item,new Chance(chance,ChanceStr));
                        sendFeedback(p,Lang.command_drop_item_add_succeed.replaceAll("\\{chance}",ChanceStr));
                        return true;
                    }
                    break;
                }
                default : {
                    sendCommandTips(sender);
                    return false;
                }
            }
        }
        sendCommandTips(sender);
        return false;
    }
    private static void sendCommandTips(CommandSender sender){
        sender.sendMessage(CommandTips1);
        sender.sendMessage(CommandTips2);
        sender.sendMessage(CommandTips3);
        sender.sendMessage(CommandTips4);
    }

}
