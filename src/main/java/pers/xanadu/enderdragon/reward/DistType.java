package pers.xanadu.enderdragon.reward;

import java.util.Map;
import java.util.TreeMap;

public enum DistType {
    killer,
    drop,//glow?
    all,
    rank,//num
    pack,//num
    termwise,
    unknown;

    private static final Map<String, DistType> mp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static DistType getByName(String str){
        //if(str == null) return drop;
        return mp.getOrDefault(str,unknown);
    }
    static {
        DistType[] values = DistType.values();
        for (DistType type : values) {
            String name = type.name();
            mp.put(name, type);
        }
    }
}
