package pers.xanadu.enderdragon.manager;

import org.bukkit.entity.Player;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.hook.HookManager;
import pers.xanadu.enderdragon.maven.DependencyManager;
import pers.xanadu.enderdragon.util.ColorUtil;

public class ActionManager {
    public static void executeAction(final Player player,final String[] args){
        String raw = getRawText(args,3);
        switch (args[2]){
            case "tell:" : {
                player.sendMessage(ColorUtil.parse(HookManager.parsePapi(player,raw)));
                return;
            }
            case "tell-colorless:" : {
                player.sendMessage(HookManager.parsePapi(player,raw));
                return;
            }
            case "tell-raw:" : {
                player.sendMessage(raw);
                return;
            }
            case "groovy:" : {
                if(DependencyManager.isGroovyLoaded()) GroovyManager.eval(raw,player);
                else Lang.sendFeedback(player,Lang.expansion_groovy_disable);
                return;
            }
        }
    }
    private static String getRawText(final String[] args,int from){
        if(from<0 || from>=args.length) throw new IllegalArgumentException("Index out of bounds!");
        StringBuilder sb = new StringBuilder(args[from]);
        for(int i=from+1;i<args.length;i++){
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
    }
}
