package pers.xanadu.enderdragon.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.util.Version;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.sqrt;
import static pers.xanadu.enderdragon.EnderDragon.getInstance;
import static pers.xanadu.enderdragon.util.MathUtil.*;

public class WorldManager {
    public static final List<String> worlds = new ArrayList<>();
    private Field dimension;
    private Field world_provider;
    private Method getDimensionID;

    public static void reload(){
        worlds.clear();
        Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
    }

    public static Collection<EnderDragon> getExplosionDragon(float power, Location loc){
        World world = loc.getWorld();
        if(world == null) return Collections.EMPTY_LIST;
        return getExplosionDragon(loc.getWorld(),power,loc.getX()+0.5d,loc.getY()+0.5d,loc.getZ()+0.5d);
    }
    public static Collection<EnderDragon> getExplosionDragon(World world,float power,double x,double y,double z){
        float f = power * 2.0F;
        int x1 = floor(x - (double)f - 1.0);
        int x2 = floor(x + (double)f + 1.0);
        int y1 = floor(y - (double)f - 1.0);
        int y2 = floor(y + (double)f + 1.0);
        int z1 = floor(z - (double)f - 1.0);
        int z2 = floor(z + (double)f + 1.0);
//        Collection<Entity> list = world.getNearbyEntities(new BoundingBox(x1, y1, z1, x2, y2, z2));
        Location cen = new Location(world,(x1+x2)/2d,(y1+y2)/2d,(z1+z2)/2d);
        double rx = (x2-x1)/2d;
        double ry = (y2-y1)/2d;
        double rz = (z2-z1)/2d;
        Collection<Entity> list = world.getNearbyEntities(cen,rx,ry,rz);
        List<EnderDragon> res = new ArrayList<>();
        for (Entity entity : list) {
            if(entity instanceof EnderDragon){
                double d0 = sqrt(c(entity,x, y, z)) / f;
                if (d0 <= 1.0) {
                    Location loc = entity.getLocation();
                    double dx = loc.getX() - x;
                    double dy = loc.getY() + 6.8d - y;
                    double dz = loc.getZ() - z;
                    double d1 = sqrt(dx * dx + dy * dy + dz * dz);
                    if (d1 != 0.0) {
                        res.add((EnderDragon) entity);
                    }
                }
            }
        }
        return res;
    }
//    public static float a(Vector endPos, Entity entity) {
//        BoundingBox bb = entity.getBoundingBox();
//        double d0 = 1.0 / ((bb.getMaxX() - bb.getMinX()) * 2.0 + 1.0);
//        double d1 = 1.0 / ((bb.getMaxY() - bb.getMinY()) * 2.0 + 1.0);
//        double d2 = 1.0 / ((bb.getMaxZ() - bb.getMinZ()) * 2.0 + 1.0);
//        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
//        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
//        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
//            int i = 0;
//            int j = 0;
//
//            for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
//                for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
//                    for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
//                        double d5 = d(f, bb.getMinX(), bb.getMaxX());
//                        double d6 = d(f1, bb.getMinY(), bb.getMaxY());
//                        double d7 = d(f2, bb.getMinZ(), bb.getMaxZ());
//                        //Vec3D vec3d1 = new Vec3D(d5 + d3, d6, d7 + d4);
//                        Location start = new Location(entity.getWorld(),d5 + d3, d6, d7 + d4);
//                        Vector dir = new Vector(endPos.getX()-start.getX(),endPos.getY()-start.getY(),endPos.getZ()-start.getZ());
//                        double maxDistance = dir.length();
//                        RayTraceResult result = entity.getWorld().rayTraceBlocks(start,dir,maxDistance, FluidCollisionMode.NEVER,false);
//                        if(result == null) ++i;
////                        if (entity.getWorld().rayTrace(new RayTrace(vec3d1, endPos, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, entity)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
////                            ++i;
////                        }
//                        ++j;
//                    }
//                }
//            }
//            return (float)i / (float)j;
//        } else {
//            return 0.0F;
//        }
//    }

    public void fixWorldEnvironment(){
        Bukkit.getWorlds().forEach(world -> {
            try{
                if(isTheEnd(world)) getInstance().getNMSManager().setEnvironment(world, World.Environment.THE_END);
            }catch(ReflectiveOperationException e){
                e.printStackTrace();
            }
        });
    }
    private boolean isTheEnd(World world) throws ReflectiveOperationException {
        String version = Version.getVersion();
        switch (version) {
            case "v1_12_R1" :
            case "v1_13_R1" : {
                Object world_server = getInstance().getNMSManager().getWorldServer(world);
                if(dimension == null) dimension = world_server.getClass().getDeclaredField("dimension");
                int dimen = (int) dimension.get(world_server);
                return dimen == 1;
            }
            case "v1_13_R2" : {
                Object world_server = getInstance().getNMSManager().getWorldServer(world);
                if(dimension == null) dimension = world_server.getClass().getField("dimension");
                Object DimensionManager = dimension.get(world_server);
                if(getDimensionID == null) getDimensionID = DimensionManager.getClass().getMethod("getDimensionID");
                int dimen = (int) getDimensionID.invoke(DimensionManager);
                return dimen == 1;
            }
            case "v1_14_R1" :
            case "v1_15_R1" : {
                Object world_server = getInstance().getNMSManager().getWorldServer(world);
                if(world_provider == null) world_provider = world_server.getClass().getField("worldProvider");
                Object worldProvider = world_provider.get(world_server);
                if(dimension == null) dimension = getInstance().getNMSManager().getWorldProviderClass().getDeclaredField("f");
                dimension.setAccessible(true);
                Object DimensionManager = dimension.get(worldProvider);
                if(getDimensionID == null) getDimensionID = DimensionManager.getClass().getMethod("getDimensionID");
                int dimen = (int) getDimensionID.invoke(DimensionManager);
                return dimen == 1;
            }
            case "v1_16_R1" :
            case "v1_16_R2" :
            case "v1_16_R3" :
            case "v1_17_R1" : {
                Object world_server = getInstance().getNMSManager().getWorldServer(world);
                if(getDimensionID == null) getDimensionID = getInstance().getNMSManager().getWorldClass().getMethod("getDimensionKey");
                Object world_type = getDimensionID.invoke(world_server);
                return world_type.toString().contains("minecraft:the_end");
            }
            default : {
                if(Version.mcMainVersion < 12){
                    Lang.warn("Your server version (" + version + ") is not supported!");
                    return false;
                }
                Object world_server = getInstance().getNMSManager().getWorldServer(world);
                if(getDimensionID == null) getDimensionID = world_server.getClass().getMethod("getTypeKey");
                Object world_type = getDimensionID.invoke(world_server);
                return world_type.toString().contains("minecraft:the_end");
            }
        }
        /*
        try{
            //>=1.18 建议使用WorldServer::getTypeKey
            Object world_server = getInstance().getNMSManager().getWorldServer(world);
            Class<?> World_Class = Class.forName("net.minecraft.server."+ Version.getVersion()+".World");//<=1.16.5
            Object world_type = World_Class.getDeclaredMethod("getDimensionKey").invoke(world_server);//<=1.17.1

            String string_type = world_type.toString();
            Bukkit.broadcastMessage(string_type);

            Class<?> ResourceKey_Class = Class.forName("net.minecraft.server."+Version.getVersion()+".ResourceKey");
            Object ResourceKey = ResourceKey_Class.cast(world_type);
            Object MinecraftKey = ResourceKey_Class.getDeclaredMethod("a").invoke(ResourceKey);
            Class<?> MinecraftKey_Class = Class.forName("net.minecraft.server."+Version.getVersion()+".MinecraftKey");
            String string_type2 = (String) MinecraftKey_Class.getDeclaredMethod("getKey").invoke(MinecraftKey);
            Bukkit.broadcastMessage(string_type2);




//            Object world_c = getCraftWorld(world);
//            Object envi = CraftWorldClass.getDeclaredMethod("getEnvironment").invoke(world_c);
//            World.Environment environment = (World.Environment) envi;
//            Bukkit.broadcastMessage(environment.toString());
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
        */
    }

}

