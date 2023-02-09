package xanadu.enderdragon.task;

import java.util.Map;
import java.util.TreeMap;

public enum TaskType {
    minute,
    hour,
    day,
    week,
    month,
    year,
    unknown;
    private static final Map<String,TaskType> mp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    public static TaskType getByName(String str){
        if(str == null) return unknown;
        return mp.getOrDefault(str,unknown);
    }
    static{
        TaskType[] values = values();
        for (TaskType taskType : values) {
            String name = taskType.name();
            mp.put(name, taskType);
        }
    }
}
