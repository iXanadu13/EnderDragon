package pers.xanadu.enderdragon.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class FileUpdater {
    public static void update() throws IOException {
        FileConfiguration config_old = plugin.getConfig();
        boolean chinese = "Chinese".equals(config_old.getString("lang"));
        File folder = new File(plugin.getDataFolder(),"setting");
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(files != null){
                for(File file : files){
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    String ver = config.getString("version");
                    if(!"2.2.0".equals(ver)) {
                        Lang.error("The version of setting/"+file.getName()+" is not supported!");
                        continue;
                    }
                    String name = file.getName();
                    Config.copyFile("setting/"+name,"new/setting/"+name,true);
                }
                File new_folder = new File(plugin.getDataFolder(),"new/setting/");
                files = new_folder.listFiles();
                if(files != null){
                    for(File file : files){
                        FileOutputStream out = new FileOutputStream(file,true);
                        if (chinese){
                            out.write(("\n\n" +
                                    "# 一些特殊的战利品\n" +
                                    "special_loot:\n" +
                                    "  # 你可以按格式添加更多条目，但是名称不能重复\n" +
                                    "  # 如果找不到执行目标(target)，所在战利品条目会被忽略\n" +
                                    "  # 目前支持的type: exp(经验), command(执行指令，可解析伤害排名)\n" +
                                    "  loot1:\n" +
                                    "    # [true/false]\n" +
                                    "    enable: false\n" +
                                    "    type: command\n" +
                                    "    # %attacker% -> 所有参与屠龙的玩家\n" +
                                    "    target: \"%attacker%\"\n" +
                                    "    # %player% -> 目标, %damage% -> 该玩家造成的总伤害\n" +
                                    "    data:\n" +
                                    "      - 'give %player% stone 1'\n" +
                                    "      - 'ed action %player% tell: 你获得了保底奖励.'\n" +
                                    "  loot2:\n" +
                                    "    enable: false\n" +
                                    "    type: command\n" +
                                    "    # %attacker_top_<rank>% -> 在屠龙中取得此排名的玩家\n" +
                                    "    target: \"%attacker_top_1%\"\n" +
                                    "    data:\n" +
                                    "      - 'ed action %player% tell: 你的伤害占比是最高的!'\n" +
                                    "      - 'ed action %player% tell: 你造成的伤害: %damage%'\n" +
                                    "  loot_exp1:\n" +
                                    "    enable: false\n" +
                                    "    type: exp\n" +
                                    "    target: \"%attacker_top_2%\"\n" +
                                    "    data:\n" +
                                    "      # 给予排名第二的玩家20点经验\n" +
                                    "      amount: 20\n" +
                                    "\n\n"
                            ).getBytes());
                        }
                        else{
                            out.write(("\n\n" +
                                    "special_loot:\n" +
                                    "  # You can add more entries with DIFFERENT name according to the format.\n" +
                                    "  # If such target not exists, this option will be ignored.\n" +
                                    "  # type: exp, command\n" +
                                    "  loot1:\n" +
                                    "    # [true/false]\n" +
                                    "    enable: false\n" +
                                    "    type: command\n" +
                                    "    # %attacker% -> all participants in dragon slaying\n" +
                                    "    target: \"%attacker%\"\n" +
                                    "    # %player% -> target, %damage% -> this player's damage to EnderDragon\n" +
                                    "    data:\n" +
                                    "      - 'give %player% stone 1'\n" +
                                    "      - 'ed action %player% tell: This is a guaranteed reward.'\n" +
                                    "  loot2:\n" +
                                    "    enable: false\n" +
                                    "    type: command\n" +
                                    "    # %attacker_top_<rank>% -> the player with exactly this rank\n" +
                                    "    target: \"%attacker_top_1%\"\n" +
                                    "    data:\n" +
                                    "      - 'ed action %player% tell: You are the one who causes the highest damage!'\n" +
                                    "      - 'ed action %player% tell: Your damage: %damage%'\n" +
                                    "  loot_exp1:\n" +
                                    "    enable: false\n" +
                                    "    type: exp\n" +
                                    "    target: \"%attacker_top_2%\"\n" +
                                    "    data:\n" +
                                    "      # add 20 exp for the rank_2 player\n" +
                                    "      amount: 20\n" +
                                    "\n\n"
                            ).getBytes());
                        }
                        out.close();
                        FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
                        fc.set("version","2.4.0");
                        fc.save(file);
                    }
                }
            }
        }
        Lang.info("New config files are generated in plugins/EnderDragon/new.");
        Lang.warn("Attention: Please confirm the accuracy before using new config!");
    }
}
