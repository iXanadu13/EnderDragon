package pers.xanadu.enderdragon.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.xanadu.enderdragon.manager.DragonManager;
import pers.xanadu.enderdragon.manager.WorldManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private static final List<String> arguments_ed = Arrays.asList("action", "drop", "reload", "respawn", "respawn_cd", "spawn", "update");
    private static final List<String> arguments_drop = Arrays.asList("add", "clear", "edit", "remove", "gui");
    private static final List<String> arguments_action = Arrays.asList("tell:","tell-colorless:","tell-raw:","groovy:");
    private static final List<String> arguments_respawn_cd = Arrays.asList("get","remove","removeAll","set","start");
    private static final List<String> arguments_reload = Arrays.asList("script");

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(arguments_ed);
        }
        else if (args.length == 2){
            if(args[0].equalsIgnoreCase("drop")) {
                result.addAll(arguments_drop);
            }
            else if(args[0].equalsIgnoreCase("respawn")){
                result.addAll(WorldManager.worlds);
            }
            else if(args[0].equalsIgnoreCase("respawn_cd")){
                result.addAll(arguments_respawn_cd);
            }
            else if(args[0].equalsIgnoreCase("reload")){
                result.addAll(arguments_reload);
            }
            else if(args[0].equalsIgnoreCase("spawn")){
                result.addAll(DragonManager.dragon_names);
            }
            else if(args[0].equalsIgnoreCase("action")){
                Stream<String> stream = Bukkit.getOnlinePlayers().stream().map(Player::getName);
                return filterResult(stream,Arrays.asList("me"),args[args.length-1]);
            }
        }
        else if(args.length == 3){
            if("drop".equals(args[0])){
                String str = args[1].toLowerCase();
                if("add".equals(str) || "clear".equals(str) || "gui".equals(str) || "remove".equals(str)){
                    result.addAll(DragonManager.dragon_names);
                }
            }
            else if("respawn_cd".equals(args[0])){
                String str = args[1].toLowerCase();
                if("get".equals(str) || "remove".equals(str) || "removeAll".equals(str) || "set".equals(str) || "start".equals(str)){
                    result.addAll(WorldManager.worlds);
                }
            }
            else if("spawn".equals(args[0])){
                result.addAll(WorldManager.worlds);
            }
            else if("action".equals(args[0])){
                result.addAll(arguments_action);
            }
        }
        return filterResult(result.stream(),args[args.length-1]);
    }

    private static List<String> filterResult(Stream<String> possible, List<String> res, final String arg){
        List<String> list = new ArrayList<>(res);
        if(arg == null) {
            possible.forEach(list::add);
            return list;
        }
        String cur = arg.toLowerCase(Locale.ROOT);
        possible.filter(suggestion-> suggestion.startsWith(cur)).forEach(list::add);
        return list;
    }
    private static List<String> filterResult(Stream<String> possible, final String arg){
        if(arg == null) return possible.collect(Collectors.toList());
        String cur = arg.toLowerCase(Locale.ROOT);
        return possible.filter(suggestion-> suggestion.startsWith(cur)).collect(Collectors.toList());
    }

}
