package pers.xanadu.enderdragon.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.manager.ItemManager;
import pers.xanadu.enderdragon.manager.TaskManager;
import pers.xanadu.enderdragon.reward.Chance;
import pers.xanadu.enderdragon.reward.Reward;
import pers.xanadu.enderdragon.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class FileUpdater {
    public static void update() throws IOException {
        Lang.warn("Nothing to update. Maybe you need '/ed migrate'?");
//        FileConfiguration config_old = plugin.getConfig();
//        boolean chinese = "Chinese".equals(config_old.getString("lang"));
//        File folder = new File(plugin.getDataFolder(),"setting");
//        if(folder.exists()){
//            File[] files = folder.listFiles();
//            if(files != null){
//                for(File file : files){
//                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//                    String ver = config.getString("version");
//                    if(!"2.2.0".equals(ver)) {
//                        Lang.error("The version of setting/"+file.getName()+" is not supported!");
//                        continue;
//                    }
//                    String name = file.getName();
//                    Config.copyFile("setting/"+name,"new/setting/"+name,true);
//                }
//                File new_folder = new File(plugin.getDataFolder(),"new/setting/");
//                files = new_folder.listFiles();
//                if(files != null){
//                    for(File file : files){
//                        FileOutputStream out = new FileOutputStream(file,true);
//                        if (chinese){
//                            out.write(("\n\n" +
//                                    "# 一些特殊的战利品\n" +
//                                    "special_loot:\n" +
//                                    "  # 你可以按格式添加更多条目，但是名称不能重复\n" +
//                                    "  # 如果找不到执行目标(target)，所在战利品条目会被忽略\n" +
//                                    "  # 目前支持的type: exp(经验), command(执行指令，可解析伤害排名)\n" +
//                                    "  loot1:\n" +
//                                    "    # [true/false]\n" +
//                                    "    enable: false\n" +
//                                    "    type: command\n" +
//                                    "    # %attacker% -> 所有参与屠龙的玩家\n" +
//                                    "    target: \"%attacker%\"\n" +
//                                    "    # %player% -> 目标, %damage% -> 该玩家造成的总伤害\n" +
//                                    "    data:\n" +
//                                    "      - 'give %player% stone 1'\n" +
//                                    "      - 'ed action %player% tell: 你获得了保底奖励.'\n" +
//                                    "  loot2:\n" +
//                                    "    enable: false\n" +
//                                    "    type: command\n" +
//                                    "    # %attacker_top_<rank>% -> 在屠龙中取得此排名的玩家\n" +
//                                    "    target: \"%attacker_top_1%\"\n" +
//                                    "    data:\n" +
//                                    "      - 'ed action %player% tell: 你的伤害占比是最高的!'\n" +
//                                    "      - 'ed action %player% tell: 你造成的伤害: %damage%'\n" +
//                                    "  loot_exp1:\n" +
//                                    "    enable: false\n" +
//                                    "    type: exp\n" +
//                                    "    target: \"%attacker_top_2%\"\n" +
//                                    "    data:\n" +
//                                    "      # 给予排名第二的玩家20点经验\n" +
//                                    "      amount: 20\n" +
//                                    "\n\n"
//                            ).getBytes());
//                        }
//                        else{
//                            out.write(("\n\n" +
//                                    "special_loot:\n" +
//                                    "  # You can add more entries with DIFFERENT name according to the format.\n" +
//                                    "  # If such target not exists, this option will be ignored.\n" +
//                                    "  # type: exp, command\n" +
//                                    "  loot1:\n" +
//                                    "    # [true/false]\n" +
//                                    "    enable: false\n" +
//                                    "    type: command\n" +
//                                    "    # %attacker% -> all participants in dragon slaying\n" +
//                                    "    target: \"%attacker%\"\n" +
//                                    "    # %player% -> target, %damage% -> this player's damage to EnderDragon\n" +
//                                    "    data:\n" +
//                                    "      - 'give %player% stone 1'\n" +
//                                    "      - 'ed action %player% tell: This is a guaranteed reward.'\n" +
//                                    "  loot2:\n" +
//                                    "    enable: false\n" +
//                                    "    type: command\n" +
//                                    "    # %attacker_top_<rank>% -> the player with exactly this rank\n" +
//                                    "    target: \"%attacker_top_1%\"\n" +
//                                    "    data:\n" +
//                                    "      - 'ed action %player% tell: You are the one who causes the highest damage!'\n" +
//                                    "      - 'ed action %player% tell: Your damage: %damage%'\n" +
//                                    "  loot_exp1:\n" +
//                                    "    enable: false\n" +
//                                    "    type: exp\n" +
//                                    "    target: \"%attacker_top_2%\"\n" +
//                                    "    data:\n" +
//                                    "      # add 20 exp for the rank_2 player\n" +
//                                    "      amount: 20\n" +
//                                    "\n\n"
//                            ).getBytes());
//                        }
//                        out.close();
//                        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
//                        fc.set("version","2.4.0");
//                        fc.save(file);
//                    }
//                }
//            }
//        }
//        Lang.info("New config files are generated in plugins/EnderDragon/new.");
//        Lang.warn("Attention: Please confirm the accuracy before using new config!");
    }
    public static void migrate(){
        boolean b1 = handleRewards();
        boolean b2 = handleGuiItems();
        if (b1 || b2) Lang.info("New reward configuration files are generated in plugins/EnderDragon/migrate.");
        else Lang.warn("No files need to be migrated!");
    }
    private static boolean handleGuiItems(){
        File folder = new File(plugin.getDataFolder(),"gui");
        if (!folder.exists()) return false;
        File[] files = folder.listFiles(f -> f.getName().endsWith(".yml"));
        if (files == null || files.length == 0) {
            return false;
        }
        for (File file : files){
            Config.copyFile(file,"new/gui/"+file.getName(),true);
            File gen_file = new File(plugin.getDataFolder(),"new/gui/"+file.getName());
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(gen_file);
            cfg.getKeys(false).forEach(key->{
                ConfigurationSection gui = cfg.getConfigurationSection(key);
                if (gui == null) return;
                ConfigurationSection Items = gui.getConfigurationSection("Items");
                if (Items == null) return;
                Items.getKeys(false).forEach(ch -> {
                    ConfigurationSection slot = Items.getConfigurationSection(ch);
                    if (slot == null) return;
                    if (!slot.contains("data")) return;
                    ItemStack itemStack = readItemStack(slot,"data");
                    ItemManager.save2section_simple(itemStack,slot,"data");
                    if (!slot.contains("data_disable")) return;
                    itemStack = readItemStack(slot,"data_disable");
                    ItemManager.save2section_simple(itemStack,slot,"data_disable");
                });
                Items.getKeys(false).forEach(ch -> {
                    ConfigurationSection slot = Items.getConfigurationSection(ch);
                    if (slot == null) return;
                    if (slot.contains("data_type")){
                        slot.set("data_type","simple");
                    }
                    else if (slot.contains("data") || slot.contains("data_disable")) {
                        slot.set("data_type","simple");
                    }
                });
            });
            try{
                cfg.save(gen_file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }
    private static ItemStack readItemStack(ConfigurationSection section,final String path){
        ItemStack itemStack;
        final String type = section.getString("data_type","");
        switch (type){
            case "nbt":
                itemStack = ItemManager.readFromNBT(section,path);
                ItemManager.getLegacy().compareAndSet(false,true);
                break;
            case "advanced":
                itemStack = ItemManager.readFromAdvData(section,path);
                ItemManager.getLegacy().compareAndSet(false,true);
                break;
            case "simple":
                itemStack = ItemManager.readFromSimple(section,path);
                break;
            default:
                itemStack = ItemManager.readFromBukkit(section,path);
        }
        return itemStack;
    }
    private static boolean handleRewards(){
        File folder = new File(plugin.getDataFolder(),"reward");
        if (!folder.exists()) return false;
        File[] files = folder.listFiles(f -> f.getName().endsWith(".yml"));
        if (files == null || files.length == 0) {
            return false;
        }
        for (File file : files){
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Config.copyFile(file,"new/reward/"+file.getName(),true);
            File gen_file = new File(plugin.getDataFolder(),"new/reward/"+file.getName());
            YamlConfiguration new_cfg = YamlConfiguration.loadConfiguration(gen_file);
            if(Config.advanced_setting_backslash_split_reward) new_cfg.options().pathSeparator('\\');
            List<String> gen = new ArrayList<>();
            List<String> list = config.getStringList("list");
            for(String str : list){
                YamlConfiguration yml = new YamlConfiguration();
                if(Config.advanced_setting_backslash_split_reward) yml.options().pathSeparator('\\');
                try{
                    yml.loadFromString(str);
                    Reward reward = ItemManager.readAsReward(yml);
                    if (reward == null) continue;
                    gen.add(saveReward(reward));
                }catch (InvalidConfigurationException e){
                    Lang.error(Lang.plugin_item_read_error + str);
                }
            }
            new_cfg.set("list",gen);
            try{
                new_cfg.save(gen_file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }
    private static String saveReward(Reward reward){
        Chance chance = reward.getChance();
        ItemStack item = reward.getItem();
        double value = chance.getValue();
        String str = chance.getStr();
        String name = reward.getName();
        YamlConfiguration yaml = new YamlConfiguration();
        if(Config.advanced_setting_backslash_split_reward) yaml.options().pathSeparator('\\');
        if(name == null){
            ItemMeta meta = item.getItemMeta();
            if(meta != null) {
                String displayName = meta.getDisplayName();
                if("".equals(displayName) || displayName == null){
                    name = item.getType().name().toLowerCase() + "(" + TaskManager.getCurrentTimeWithSpecialFormat() + ")";
                }
                else name = meta.getDisplayName();
            }
            else name = item.getType().name().toLowerCase() + "(" + TaskManager.getCurrentTimeWithSpecialFormat() + ")";
        }
        ConfigurationSection section = yaml.createSection(name);
        section.set("data_type", "default");
        section.set("data", item);
        ConfigurationSection drop_section = section.createSection("drop_chance");
        drop_section.set("value",value);
        drop_section.set("format",str);
        return yaml.saveToString();
    }
}
