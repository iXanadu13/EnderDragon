package pers.xanadu.enderdragon.manager;

import pers.xanadu.enderdragon.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DamageManager {
    public static final ConcurrentHashMap<UUID, ConcurrentHashMap<String,Double>> data = new ConcurrentHashMap<>();
    public static List<Pair<String,Double>> getDamageList(UUID uuid){
        ConcurrentHashMap<String,Double> mp = data.get(uuid);
        List<Pair<String,Double>> list = new ArrayList<>();
        if(mp == null) return list;
        mp.forEach((k,v)->list.add(new Pair<>(k,v)));
        return list;
    }
    public static <T> int sortByDamage(Pair<T, Double> p1, Pair<T, Double> p2){
        if(p2.second>p1.second) return 1;
        if(p2.second.equals(p1.second)) return 0;
        return -1;
    }

}
