package xanadu.enderdragon.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static xanadu.enderdragon.manager.DragonManager.dragon_names;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private static final List<String> arguments_1 = List.of(new String[]{"drop", "reload", "respawn", "update"});
    private static final List<String> arguments_2 = List.of(new String[]{"add", "clear", "gui"});
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String s : arguments_1) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }
        else if (args.length == 2){
            if(args[0].equalsIgnoreCase("drop")) {
                for (String s : arguments_2) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            return result;
        }
        else if(args.length == 3){
            String str = args[1].toLowerCase();
            if("add".equals(str) || "clear".equals(str) || "gui".equals(str)){
                for (String s : dragon_names) {
                    if (s.toLowerCase().startsWith(args[2].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            return result;
        }
        return null;
    }

}
