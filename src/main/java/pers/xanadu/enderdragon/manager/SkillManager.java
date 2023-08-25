package pers.xanadu.enderdragon.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import pers.xanadu.enderdragon.util.MathUtil;

import java.util.concurrent.ThreadLocalRandom;

public class SkillManager {
    //离开祭坛
    //向指定位置发射末影龙火球
    //在玩家附近召唤愤怒的末影人
    //立刻飞向玩家并近战攻击
    //在当前位置吐龙息

    public static void leaveEndPortal(final EnderDragon dragon){
        dragon.setPhase(EnderDragon.Phase.LEAVE_PORTAL);
    }
    public static void launchDragonFireball(final EnderDragon dragon, final Location target){
        Location loc1 = dragon.getLocation();
        World world = loc1.getWorld();
        if(world == null) return;
        double dx = target.getX() - loc1.getX();
        double dy = target.getY() - loc1.getY();
        double dz = target.getZ() - loc1.getZ();
        double yaw = Math.toDegrees(-Math.atan2(dx, dz));
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double pitch = Math.toDegrees(-Math.atan2(dy, horizontalDistance));
        yaw = normalizeAngle(yaw, -180, 180);
        pitch = normalizeAngle(pitch, -90, 90);
        Location loc = new Location(world,loc1.getX(),loc1.getY(),loc1.getZ(), (float) yaw, (float) pitch);
        Projectile projectile = world.spawn(loc, DragonFireball.class);
        projectile.setShooter(dragon);

        //Projectile projectile2 = loc1.getWorld().spawn(loc, DragonFireball.class);
        //projectile2.setShooter(dragon);
        //projectile2.setVelocity(new Vector(-dx,-dy,-dz).normalize());
    }
    public static void callEnderManReinforce(final Player p,int num){
        if(num <= 0) return;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location loc = p.getLocation();
        World world = loc.getWorld();
        assert world != null;
        int i = MathUtil.floor(loc.getX());
        int j = MathUtil.floor(loc.getY());
        int k = MathUtil.floor(loc.getZ());
        int i1=10,j1,k1=10;
        for (int l = 0; l < 50; l++) {
            i1 = i + (random.nextInt(7, 41) * random.nextInt(-1, 2));
            j1 = j + (random.nextInt(7, 41) * random.nextInt(-1, 2));
            k1 = k + (random.nextInt(7, 41) * random.nextInt(-1, 2));
            Block block = world.getBlockAt(i1,j1-1,k1);
            if(block.isLiquid() || block.isEmpty()) continue;
            if(!world.getBlockAt(i1,j1,k1).isEmpty()) continue;
            if(!world.getBlockAt(i1,j1+1,k1).isEmpty()) continue;
            if(!world.getBlockAt(i1,j1+2,k1).isEmpty()) continue;
            Enderman enderman = (Enderman) world.spawnEntity(new Location(world,i1,j1,k1),EntityType.ENDERMAN);
            enderman.setTarget(p);
            --num;
            if(num == 0) return;
        }
        loc = world.getHighestBlockAt(i1,k1).getLocation().add(0,1,0);
        while (num>0){
            --num;
            Enderman enderman = (Enderman) world.spawnEntity(loc,EntityType.ENDERMAN);
            enderman.setTarget(p);
        }
    }

    private static double normalizeAngle(double angle, double min, double max) {
        if (angle < min) {
            angle = min;
        } else if (angle > max) {
            angle = max;
        }
        return angle;
    }
}
