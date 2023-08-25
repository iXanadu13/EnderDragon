package pers.xanadu.enderdragon.manager;

import org.bukkit.configuration.ConfigurationSection;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.task.DragonRespawnRunnable;
import pers.xanadu.enderdragon.task.type.*;
import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class TaskManager {
    public static final ConcurrentHashMap<String, DragonRespawnRunnable> mp = new ConcurrentHashMap<>();
    private static final DateTimeFormatter RoundTimeFormat_hm = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter RoundTimeFormat_all = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Task parse(String unique_name,String world_name,String string){
        String[] str = string.split(":",2);
        TaskType taskType = TaskType.getByName(str[0]);
        switch (taskType){
            case minute : {
                return new Minute(TaskType.minute,unique_name,world_name,str[1]);
            }
            case hour : {
                return new Hour(TaskType.hour,unique_name,world_name,str[1]);
            }
            case day : {
                return new Day(TaskType.day,unique_name,world_name,str[1]);
            }
            case week : {
                return new Week(TaskType.week,unique_name,world_name,str[1]);
            }
            case month : {
                return new Month(TaskType.month,unique_name,world_name,str[1]);
            }
            case year : {
                return new Year(TaskType.year,unique_name,world_name,str[1]);
            }
            default : {
                return null;
            }
        }
    }
    public static void reload(){
        mp.forEach((key,runnable)->runnable.cancel());
        mp.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("auto_respawn");
        if(section==null) return;
        int cnt = 0;
        for (String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if(sub == null) continue;
            boolean enable = sub.getBoolean("enable");
            String world_name = sub.getString("world_name");
            String task_string = sub.getString("respawn_time");
            if(!enable || task_string == null || world_name == null) continue;
            DragonRespawnRunnable runnable = new DragonRespawnRunnable(parse(key,world_name,task_string));
            runnable.start();
            mp.put(key,runnable);
            ++cnt;
        }
        Lang.info(String.format("%d auto_respawn task(s) started to run...",cnt));
    }
    public static LocalTime getRoundTime(String str){
        try {
            return LocalTime.parse(str, RoundTimeFormat_hm);
        }catch (DateTimeParseException e){
            Lang.error("\"respawn_time\" in config.yml error!The format of time should be HH:mm.");
        }
        return null;
    }
    public static String getRoundTimeStr(LocalDateTime time){
        return time.format(RoundTimeFormat_all);
    }
    public static LocalDateTime getLocalDateTime(String str){
        if(isValidTime(str)) return LocalDateTime.parse(str, RoundTimeFormat_all);
        return null;
    }
    public static boolean isValidTime(String str){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try{
            df.parse(str);
        } catch (ParseException e) {
            Lang.error("\"next_respawn_time\" in data.yml error!The format of time should be HH:mm.");
            return false;
        }
        return true;
    }
    public static String getCurrentTimeWithSpecialFormat(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH∶mm∶ss");//这里的∶是特殊字符
        return df.format(new Date());
    }

}
