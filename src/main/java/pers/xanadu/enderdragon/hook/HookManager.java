package pers.xanadu.enderdragon.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.hook.placeholderapi.Papi;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class HookManager {
    private static boolean papi = false;
    public static void init(){
        if(pm.getPlugin("PlaceholderAPI") != null){
            Lang.info("Hooking to PlaceholderAPI...");
            new Papi(getInstance());
            papi = true;
        }
    }
    public static boolean isPlaceholderAPILoaded(){
        return papi;
    }
    public static String parsePapi(final OfflinePlayer p, String str){
        if(papi) return PlaceholderAPI.setPlaceholders(p,str);
        return str;
    }
}
