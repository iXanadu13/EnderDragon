package xanadu.enderdragon.manager;

import xanadu.enderdragon.config.Config;
import xanadu.enderdragon.config.Lang;
import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;
import xanadu.enderdragon.task.types.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static xanadu.enderdragon.EnderDragon.*;
import static xanadu.enderdragon.config.Lang.error;
import static xanadu.enderdragon.config.Lang.plugin_file_save_error;

public class TaskManager {
    public static String path = "auto_respawn.next_respawn_time";
    public static Task task = null;
    public static Task parse(String string){
        String[] str = string.split(":",2);
        TaskType taskType = TaskType.getByName(str[0]);
        switch (taskType){
            case minute -> {
                return new Minute(TaskType.minute,str[1]);
            }
            case hour -> {
                return new Hour(TaskType.hour,str[1]);
            }
            case day -> {
                return new Day(TaskType.day,str[1]);
            }
            case week -> {
                return new Week(TaskType.week,str[1]);
            }
            case month -> {
                return new Month(TaskType.month,str[1]);
            }
            case year -> {
                return new Year(TaskType.year,str[1]);
            }
            default -> {
                return null;
            }
        }
    }
    public static void reload(){
        task = parse(Config.auto_respawn_respawn_time);
    }
    public static LocalTime getRoundTime(String str){
        try {
            return LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm"));
        }catch (DateTimeParseException e){
            Lang.error("\"respawn_time\" in config.yml error!The format of time should be HH:mm.");
        }
        return null;
    }
    public static String getRoundTimeStr(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    public static LocalDateTime getLocalDateTime(String str){
        if(isValidTime(str)) return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
    public static void saveFile(LocalDateTime nextTime){
        data.set(TaskManager.path,getRoundTimeStr(nextTime));
        try{
            data.save(dataF);
        }catch (IOException ex){
            error(plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
        }
    }
    public static String getCurrentTimeWithSpecialFormat(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH∶mm∶ss");
        return df.format(new Date());
    }

}
