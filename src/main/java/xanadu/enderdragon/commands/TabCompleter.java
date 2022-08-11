package xanadu.enderdragon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    List<String> arguments = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            arguments.clear();
            arguments.add("reload");
            arguments.add("drop");
            for (String s : arguments) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(s);
                }
            }
            return result;
        }
        else if (args.length == 2){
            arguments.clear();
            arguments.add("add");
            arguments.add("clear");
            arguments.add("gui");
            if(args[0].equalsIgnoreCase("drop")) {
                for (String s : arguments) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        result.add(s);
                    }
                }
            }
            return result;
        }
        return null;
    }

}
