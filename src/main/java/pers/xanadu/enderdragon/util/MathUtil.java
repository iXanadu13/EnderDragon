package pers.xanadu.enderdragon.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class MathUtil {
    public static int floor(double d) {
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }
    public static double c(final Entity entity, double x, double y, double z) {
        Location loc = entity.getLocation();
        double d0 = loc.getX() - x;
        double d1 = loc.getY() - y;
        double d2 = loc.getZ() - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
    public static double div(double d1,double d2,int len){
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2,len, RoundingMode.HALF_UP).doubleValue();
    }
    public static int[] hexToInt(final String hex){
        int[] result = new int[3];
        String s1 = hex.substring(0,2);
        String s2 = hex.substring(2,4);
        String s3 = hex.substring(4,6);
        result[0] = Integer.parseInt(s1,16);
        result[1] = Integer.parseInt(s2,16);
        result[2] = Integer.parseInt(s3,16);
        return result;
    }
    public static String locationToString(final Location loc){
        if (loc.getWorld() == null) return "";
        return loc.getWorld().getName()+";"+loc.getX()+";"+loc.getY()+";"+loc.getZ();
    }
    public static Location stringToLocation(final String str){
        final String[] parts = str.split(";");
        if(parts.length != 4) return null;
        final World world = Bukkit.getWorld(parts[0]);
        final double x = Double.parseDouble(parts[1]);
        final double y = Double.parseDouble(parts[2]);
        final double z = Double.parseDouble(parts[3]);
        return new Location(world,x,y,z);
    }
    public static String formatDuration(int seconds) {
        if (seconds < 0) throw new IllegalArgumentException("Duration must be a positive number.");
        int days = seconds / (24 * 3600);
        int hours = (seconds % (24 * 3600)) / 3600;
        int minutes = ((seconds % (24 * 3600)) % 3600) / 60;
        int remainingSeconds = ((seconds % (24 * 3600)) % 3600) % 60;
        StringBuilder duration = new StringBuilder();
        boolean f = false;
        if(days>0){
            duration.append(days).append("d");
            f = true;
        }
        if(f||hours>0){
            duration.append(hours).append("h");
            f = true;
        }
        if(f||minutes>0){
            duration.append(minutes).append("m");
        }
        duration.append(remainingSeconds).append("s");
        return duration.toString();
    }
    public static int[] getDate(int seconds){
        if (seconds < 0) throw new IllegalArgumentException("Duration must be a positive number.");
        int days = seconds / (24 * 3600);
        int hours = (seconds % (24 * 3600)) / 3600;
        int minutes = ((seconds % (24 * 3600)) % 3600) / 60;
        int remainingSeconds = ((seconds % (24 * 3600)) % 3600) % 60;
        return new int[]{days,hours,minutes,remainingSeconds};
    }
    @Deprecated
    public static <K> K getByWeight(final ConcurrentHashMap<K,Double> mp){
        double sum = mp.values().stream().mapToDouble(Double::doubleValue).sum();
        double rand = ThreadLocalRandom.current().nextDouble(sum);
        final AtomicReference<Double> cur = new AtomicReference<>(0d);
        final AtomicReference<K> res = new AtomicReference<>();
        mp.forEach((k,v)->{
            Double value = cur.get();
            if(value<=rand && value+v>rand) {
                res.set(k);
                return;
            }
            cur.updateAndGet(v1 -> v1 + v);
        });
        return res.get();
    }
}
