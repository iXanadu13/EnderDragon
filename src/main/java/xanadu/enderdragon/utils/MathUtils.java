package xanadu.enderdragon.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import static xanadu.enderdragon.EnderDragon.server;

public class MathUtils {
    public static double div(double d1,double d2,int len){
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2,len, RoundingMode.HALF_UP).doubleValue();
    }
    public static int[] hexToInt(String hex){
        int[] result = new int[3];
        String s1 = hex.substring(0,2);
        String s2 = hex.substring(2,4);
        String s3 = hex.substring(4,6);
        result[0] = Integer.parseInt(s1,16);
        result[1] = Integer.parseInt(s2,16);
        result[2] = Integer.parseInt(s3,16);
        return result;
    }
    public static String locationToString(Location loc){
        if (loc.getWorld() == null) return "";
        return loc.getWorld().getName()+";"+loc.getX()+";"+loc.getY()+";"+loc.getZ();
    }
    public static Location stringToLocation(String str){
        final String[] parts = str.split(";");
        if(parts.length != 4) return null;
        final World world = server.getWorld(parts[0]);
        final double x = Double.parseDouble(parts[1]);
        final double y = Double.parseDouble(parts[2]);
        final double z = Double.parseDouble(parts[3]);
        return new Location(world,x,y,z);
    }
}
