package pers.xanadu.enderdragon.util;

import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.nms.BossBar.I_BossBarManager;
import pers.xanadu.enderdragon.nms.NMSItem.I_NMSItemManager;
import pers.xanadu.enderdragon.nms.RespawnAnchor.I_RespawnAnchorManager;
import pers.xanadu.enderdragon.nms.WorldData.I_WorldDataManager;

import static org.bukkit.Bukkit.getServer;

public class Version {
    public static final String setting_dragon = "2.2.0";
    public static final String lang = "2.2.0";
    public static final String config = "2.2.0";
    public static final String data = "2.2.0";
    public static int mcMainVersion;
    public static int mcPatchVersion;
    private static String version = "no version found";
    private static boolean isMohist = false;
    public static void init(){
        try {
            version = getServer().getClass().getPackage().getName().split("\\.")[3];
            String[] mc_version = getServer().getBukkitVersion().split("-")[0].split("\\.");
            mcMainVersion = Integer.parseInt(mc_version[1]);
            if(mc_version.length<=2) mcPatchVersion = 0;
            else mcPatchVersion = Integer.parseInt(mc_version[2]);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Lang.warn("Failed to get nms-version!");
        }
        Lang.info("Found version: " + version);
        try{
            Class.forName("net.minecraftforge.server.ServerMain");
            isMohist = true;
        }catch(ReflectiveOperationException ignored){}

    }
    public static I_NMSItemManager getNMSItemManager(){
        switch (version){
            case "v1_12_R1" : return new pers.xanadu.enderdragon.nms.NMSItem.v1_12_R1.NMSItemManager();
            case "v1_13_R1" : return new pers.xanadu.enderdragon.nms.NMSItem.v1_13_R1.NMSItemManager();
            default: return new pers.xanadu.enderdragon.nms.NMSItem.v1_13_R2_above.NMSItemManager();
        }
    }
    public static I_BossBarManager getBossBarManager(){
        switch(version){
            case "v1_12_R1" : return new pers.xanadu.enderdragon.nms.BossBar.v1_12_R1.BossBarManager();
            case "v1_13_R1" : return new pers.xanadu.enderdragon.nms.BossBar.v1_13_R1.BossBarManager();
            case "v1_13_R2" : return new pers.xanadu.enderdragon.nms.BossBar.v1_13_R2.BossBarManager();
            default: return null;
        }
    }
    public static I_WorldDataManager getWorldDataManager(){
        switch (version){
            case "v1_12_R1" : return new pers.xanadu.enderdragon.nms.WorldData.v1_12_R1.WorldDataManager();
            case "v1_13_R1" : return new pers.xanadu.enderdragon.nms.WorldData.v1_13_R1.WorldDataManager();
            case "v1_13_R2" : return new pers.xanadu.enderdragon.nms.WorldData.v1_13_R2.WorldDataManager();
            case "v1_14_R1" : return new pers.xanadu.enderdragon.nms.WorldData.v1_14_R1.WorldDataManager();
            case "v1_15_R1" : return new pers.xanadu.enderdragon.nms.WorldData.v1_15_R1.WorldDataManager();
            case "v1_16_R1" : return new pers.xanadu.enderdragon.nms.WorldData.v1_16_R1.WorldDataManager();
            case "v1_16_R2" : return new pers.xanadu.enderdragon.nms.WorldData.v1_16_R2.WorldDataManager();
            case "v1_16_R3" : return new pers.xanadu.enderdragon.nms.WorldData.v1_16_R3.WorldDataManager();
            default : return new pers.xanadu.enderdragon.nms.WorldData.v1_17_above.WorldDataManager();
        }
    }
    public static I_RespawnAnchorManager getRespawnAnchorManager(){
        switch (version){
            case "v1_12_R1" :
            case "v1_13_R1" :
            case "v1_14_R1" :
            case "v1_13_R2" :
            case "v1_15_R1" : return null;
            case "v1_16_R1" : return new pers.xanadu.enderdragon.nms.RespawnAnchor.v1_16_R1.RespawnAnchorManager();
            case "v1_16_R2" : return new pers.xanadu.enderdragon.nms.RespawnAnchor.v1_16_R2.RespawnAnchorManager();
            case "v1_16_R3" : return new pers.xanadu.enderdragon.nms.RespawnAnchor.v1_16_R3.RespawnAnchorManager();
            case "v1_17_R1" : return new pers.xanadu.enderdragon.nms.RespawnAnchor.v1_17_R1.RespawnAnchorManager();
            default: return new pers.xanadu.enderdragon.nms.RespawnAnchor.v1_18_above.RespawnAnchorManager();
        }
    }
    public static String getVersion() {
        return version;
    }
    public static boolean isMohist(){
        return isMohist;
    }

}