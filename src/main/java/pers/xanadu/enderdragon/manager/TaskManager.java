package pers.xanadu.enderdragon.manager;

import pers.xanadu.enderdragon.config.Config;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.task.type.*;
import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static pers.xanadu.enderdragon.EnderDragon.*;

public class TaskManager {
    private static final DateTimeFormatter RoundTimeFormat_hm = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter RoundTimeFormat_all = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static String path = "auto_respawn.next_respawn_time";
    public static Task task = null;
    public static Task parse(String string){
        String[] str = string.split(":",2);
        TaskType taskType = TaskType.getByName(str[0]);
        switch (taskType){
            case minute : {
                return new Minute(TaskType.minute,str[1]);
            }
            case hour : {
                return new Hour(TaskType.hour,str[1]);
            }
            case day : {
                return new Day(TaskType.day,str[1]);
            }
            case week : {
                return new Week(TaskType.week,str[1]);
            }
            case month : {
                return new Month(TaskType.month,str[1]);
            }
            case year : {
                return new Year(TaskType.year,str[1]);
            }
            default : {
                return null;
            }
        }
    }
    public static void reload(){
        task = parse(Config.auto_respawn_respawn_time);
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
    public static void saveFile(LocalDateTime nextTime){
        data.set(TaskManager.path,getRoundTimeStr(nextTime));
        try{
            data.save(dataF);
        }catch (IOException ex){
            Lang.error(Lang.plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
        }
    }
    public static String getCurrentTimeWithSpecialFormat(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH∶mm∶ss");//这里的∶是特殊字符
        return df.format(new Date());
    }

}
