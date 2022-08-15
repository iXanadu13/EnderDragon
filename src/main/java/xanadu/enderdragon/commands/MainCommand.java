package xanadu.enderdragon.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xanadu.enderdragon.lang.Message;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static xanadu.enderdragon.EnderDragon.*;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("ed.reload")){
                    sender.sendMessage(Message.NoCommandPermission);
                    return false;
                }
                if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
                    plugin.saveDefaultConfig();
                }
                if (!new File(plugin.getDataFolder(), "data.yml").exists()) {
                    plugin.saveResource("data.yml", false);
                }
                plugin.reloadConfig();
                data = YamlConfiguration.loadConfiguration(dataF);
                language = plugin.getConfig().getString("lang","Chinese");
                langPath = "lang/" + language + ".yml";
                if (!new File(plugin.getDataFolder(), langPath).exists()) {
                    plugin.saveResource(langPath, false);
                }
                langF = new File(plugin.getDataFolder(), langPath);
                lang = YamlConfiguration.loadConfiguration(langF);
                Message.loadLanguage();
                sender.sendMessage(prefix + Message.configReloaded);
            }
        }
        else if (args.length > 1 && args[0].equalsIgnoreCase("drop") ) {
            if (!(sender instanceof Player || args[1].equalsIgnoreCase("clear"))) {
                    sender.sendMessage(prefix + Message.PlayerCommand);
                    return false;
            }
            if (args[1].equalsIgnoreCase("add")) {
                if(!sender.hasPermission("ed.drop.edit")){
                    sender.sendMessage(Message.NoCommandPermission);
                    return false;
                }
                if(args.length == 3 ) {
                    Player p = (Player) sender;
                    if(p.getItemInHand().getType() == Material.AIR){
                        p.sendMessage(prefix + Message.DropItemAddFail);
                        return false;
                    }
                    String ChanceStr = args[2];
                    double chance = 0;
                    try {
                        chance = Double.parseDouble(ChanceStr);
                    } catch (NumberFormatException ex){
                        p.sendMessage(prefix + Message.MustNumber + ChanceStr);
                        throw new NumberFormatException("\n\n"
                                + "\33[31;1m" + "Error: /ed drop add " + ChanceStr + "\n"
                                + "\33[33;1m" + "Valid: /ed drop add " +Message.Number+ "\n"
                                + "\33[0m");
                    }
                    List<String> datum = data.getStringList("items");
                    ItemStack item = p.getItemInHand();
                    datum.add(itemStackToString(item));
                    datum.add(String.valueOf(chance));
                    data.set("items", datum);
                    try {
                        data.save(dataF);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    data = YamlConfiguration.loadConfiguration(dataF);
                    p.sendMessage(prefix + Message.DropItemAddSucceed.replace("{chance}",""+chance));

                }
            }
            if (args[1].equalsIgnoreCase("clear")) {
                if(!sender.hasPermission("ed.drop.edit")){
                    sender.sendMessage(Message.NoCommandPermission);
                    return false;
                }
                if(args.length == 2 ) {
                    data.set("items","");
                    try {
                        data.save(dataF);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    data = YamlConfiguration.loadConfiguration(dataF);
                    sender.sendMessage(prefix + Message.ClearDropItemConfig);
                }
            }
            if (args[1].equalsIgnoreCase("gui")) {
                if(!sender.hasPermission("ed.drop.gui")){
                    sender.sendMessage(Message.NoCommandPermission);
                    return false;
                }
                if(args.length == 2 ) {
                    Player p = (Player) sender;
                    String title = plugin.getConfig().getString("special-dragon.drop-gui-title");
                    if(title == null){title = "";}
                    Inventory inv = Bukkit.createInventory(null,54,title);
                    List<String> datum = data.getStringList("items");
                    int max = datum.size() / 2 ;
                    for(int i=0 ; i<max ; ){
                        i = i + 1;
                        double chance = Double.parseDouble(datum.get(i*2-1));
                        String str = datum.get(i*2-2);
                        ItemStack item = StringToItemStack(str);
                        ItemMeta meta = item.getItemMeta();
                        if(meta != null) {
                            List<String> lore = meta.getLore();
                            if(lore != null) {
                                lore.add("§6("+Message.DropChance+": " + chance + "%)§r");
                                meta.setLore(lore);
                            }
                            else{
                                meta.setLore(Collections.singletonList("§6("+Message.DropChance+": " + chance + "%)§r"));
                            }
                            item.setItemMeta(meta);
                        }
                        inv.setItem(i-1,item);
                        if(i == 54){break;}
                    }
                    p.openInventory(inv);

                }
            }

        }
        else {
            sender.sendMessage(Message.CommandTips1);
            sender.sendMessage(Message.CommandTips2);
        }
        return false;
    }

}
