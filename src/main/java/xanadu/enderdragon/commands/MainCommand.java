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
                    sender.sendMessage("§4你没有使用该命令的权限");
                    return false;
                }
                plugin.reloadConfig();
                data = YamlConfiguration.loadConfiguration(data0);
                language = YamlConfiguration.loadConfiguration(language0);
                prefix = language.getString("prefix");
                sender.sendMessage(prefix + "§a配置文件已重载");
            }
        }
        else if (args[0].equalsIgnoreCase("drop") ) {
            if (!(sender instanceof Player || args[1].equalsIgnoreCase("clear"))) {
                    sender.sendMessage(prefix + "§c这个指令只能由玩家执行");
                    return false;
            }
            if (args[1].equalsIgnoreCase("add")) {
                if(!sender.hasPermission("ed.drop.change")){
                    sender.sendMessage("§4你没有使用该命令的权限");
                    return false;
                }
                if(args.length == 3 ) {
                    Player p = (Player) sender;
                    if(p.getItemInHand().getType() == Material.AIR){
                        p.sendMessage(prefix + "§c添加掉落物失败，你手上没有拿物品...");
                        return false;
                    }
                    String ChanceStr = args[2];
                    double chance = 0;
                    try {
                        chance = Double.parseDouble(ChanceStr);
                    } catch (NumberFormatException ex){
                        p.sendMessage(prefix + "§c您应该输入数字而不是 " + ChanceStr);
                        throw new NumberFormatException("\n\n"
                                + "\33[31;1m" + "错误的命令: /ed drop add " + ChanceStr + "\n"
                                + "\33[33;1m" + "正确用法: /ed drop add 数字" + "\n"
                                + "\33[0m");
                    }
                    List<String> datum = data.getStringList("items");
                    ItemStack item = p.getItemInHand();
                    datum.add(itemStackToString(item));
                    datum.add(String.valueOf(chance));
                    data.set("items", datum);
                    try {
                        data.save(data0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    data = YamlConfiguration.loadConfiguration(data0);
                    p.sendMessage(prefix + "§a掉落物添加成功，概率为: §c" + chance + "%");

                }
            }
            if (args[1].equalsIgnoreCase("clear")) {
                if(!sender.hasPermission("ed.drop.change")){
                    sender.sendMessage("§4你没有使用该命令的权限");
                    return false;
                }
                if(args.length == 2 ) {
                    data.set("items","");
                    try {
                        data.save(data0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    data = YamlConfiguration.loadConfiguration(data0);
                    sender.sendMessage(prefix + "§a掉落物配置已清空");
                }
            }
            if (args[1].equalsIgnoreCase("gui")) {
                if(!sender.hasPermission("ed.drop.gui")){
                    sender.sendMessage("§4你没有使用该命令的权限");
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
                                lore.add("§6(掉落概率: " + chance + "%)§r");
                                meta.setLore(lore);
                            }
                            else{
                                meta.setLore(Collections.singletonList("§6(掉落概率: " + chance + "%)§r"));
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
            sender.sendMessage("§e/ed reload 重载配置文件");
            sender.sendMessage("§e/ed drop 特殊龙掉落物设置");
        }
        return false;
    }

}
