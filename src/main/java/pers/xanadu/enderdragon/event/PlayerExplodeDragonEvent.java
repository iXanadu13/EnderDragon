package pers.xanadu.enderdragon.event;

import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

public final class PlayerExplodeDragonEvent {
    long _time;
    Player _player;
    EnderDragon _dragon;
    Location _loc;
    public PlayerExplodeDragonEvent(long time,Player player,EnderDragon dragon,Location loc){
        _time = time;
        _player = player;
        _dragon = dragon;
        _loc = loc;
    }
    public long getTime(){
        return _time;
    }
    public Player getPlayer(){
        return _player;
    }
    public EnderDragon getEnderDragon(){
        return _dragon;
    }
    public Location getLocation(){
        return _loc;
    }
//    private static final class MyComparator implements Comparator<PlayerExplodeDragonEvent>{
//        @Override
//        public int compare(final PlayerExplodeDragonEvent e1,final PlayerExplodeDragonEvent e2){
//            if(e2._time>e1._time) return 1;
//            else if(e2._time==e1._time) return 0;
//            return -1;
//        }
//    }
}
