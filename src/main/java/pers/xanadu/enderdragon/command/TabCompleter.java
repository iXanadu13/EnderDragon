package pers.xanadu.enderdragon.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.WorldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private static final List<String> arguments_ed = Arrays.asList("drop", "reload", "respawn", "respawn_cd", "update");
    private static final List<String> arguments_drop = Arrays.asList("add", "clear", "edit", "remove", "gui");
    private static final List<String> arguments_respawn_cd = Arrays.asList("get","remove","removeAll","set","start");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String s : arguments_ed) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }
        else if (args.length == 2){
            if(args[0].equalsIgnoreCase("drop")) {
                for (String s : arguments_drop) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("respawn")){
                for(String s : WorldManager.worlds){
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            else if(args[0].equalsIgnoreCase("respawn_cd")){
                for(String s : arguments_respawn_cd){
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            return result;
        }
        else if(args.length == 3){
            if("drop".equals(args[0])){
                String str = args[1].toLowerCase();
                if("add".equals(str) || "clear".equals(str) || "gui".equals(str) || "remove".equals(str)){
                    for (String s : DragonManager.dragon_names) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            result.add(s);
                        }
                    }
                }
            }
            else if("respawn_cd".equals(args[0])){
                String str = args[1].toLowerCase();
                if("get".equals(str) || "remove".equals(str) || "removeAll".equals(str) || "set".equals(str) || "start".equals(str)){
                    for (String s : WorldManager.worlds) {
                        if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                            result.add(s);
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

}
