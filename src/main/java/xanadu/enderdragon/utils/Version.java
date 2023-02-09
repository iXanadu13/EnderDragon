package xanadu.enderdragon.utils;

import org.bukkit.Bukkit;

import static xanadu.enderdragon.config.Lang.info;
import static xanadu.enderdragon.config.Lang.warn;

public class Version {
    private static String version = "no version found";
    public static void init(){
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException exception) {
            warn("ArrayIndexOutOfBoundsExceptions, please make sure the path is correct and exists!");
        }
        info("Found version: " + version);
    }
    public static String getVersion() {
        return version;
    }

}
