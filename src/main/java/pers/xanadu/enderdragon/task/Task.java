package pers.xanadu.enderdragon.task;

import pers.xanadu.enderdragon.EnderDragon;
import pers.xanadu.enderdragon.config.Lang;
import pers.xanadu.enderdragon.manager.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static pers.xanadu.enderdragon.EnderDragon.data;
import static pers.xanadu.enderdragon.EnderDragon.dataF;
import static pers.xanadu.enderdragon.manager.TaskManager.getRoundTimeStr;

public abstract class Task {
    protected int period;
    protected int day;
    private final TaskType type;
    protected abstract LocalDateTime initNextTime(LocalDateTime cur);
    public abstract void updateTime();
    protected LocalDateTime nextTime;
    protected final String world_name;
    protected final String unique_name;
    protected Task(TaskType type, String unique_name,String world_name,String str){
        this.type = type;
        this.unique_name = unique_name;
        this.world_name = world_name;
        switch (type){
            case minute :
            case hour : {
                this.period = Integer.parseInt(str);
                break;
            }
            case day : {
                this.period = Integer.parseInt(str.split(",")[0]);
                break;
            }
            case week :
            case month :
            case year : {
                this.day = Integer.parseInt(str.split(",")[0]);
                break;
            }
        }
        char split = data.options().pathSeparator();
        String next_time = EnderDragon.data.getString("auto_respawn"+split+unique_name+split+"next_respawn_time");
        if(next_time == null){
            switch (type){
                case minute :
                case hour : {
                    this.nextTime = this.initNextTime(LocalDateTime.now().withSecond(0));
                    break;
                }
                case day :
                case week :
                case month :
                case year : {
                    calcNextTime(str);
                    break;
                }
            }
            this.saveFile();
        }
        else this.nextTime = TaskManager.getLocalDateTime(next_time);
    }
    private void calcNextTime(String str){
        LocalTime time = TaskManager.getRoundTime(str.split(",")[1]);
        if(time != null) this.nextTime = this.initNextTime(LocalDateTime.now().withSecond(0).with(time));
        else this.nextTime = this.initNextTime(LocalDateTime.now().withSecond(0));
    }
    public boolean isTimeUp(){
        LocalDateTime now = LocalDateTime.now().plusSeconds(1);
        if(now.isAfter(nextTime)){
            return true;
        }
        return false;
    }
    public void saveFile(){
        char split = data.options().pathSeparator();
        String path = "auto_respawn"+split+unique_name+split;
        data.set(path+"world_name",world_name);
        data.set(path+"next_respawn_time",getRoundTimeStr(nextTime));
        try{
            data.save(dataF);
        }catch (IOException ex){
            Lang.error(Lang.plugin_file_save_error.replaceAll("\\{file_name}",dataF.getName()));
        }
    }

    public TaskType getType(){
        return this.type;
    }

}
