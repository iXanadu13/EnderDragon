package xanadu.enderdragon.task;

import xanadu.enderdragon.manager.TaskManager;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static xanadu.enderdragon.EnderDragon.data;
import static xanadu.enderdragon.manager.TaskManager.*;

public abstract class Task {
    protected int period;
    protected int day;
    private final TaskType type;
    public abstract LocalDateTime getNextTime(LocalDateTime cur);
    public abstract void updateTime();
    protected LocalDateTime nextTime;
    protected Task(TaskType type,String str){
        this.type = type;
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
        if(data.getString(path) == null){
            switch (type){
                case minute :
                case hour : {
                    this.nextTime = this.getNextTime(LocalDateTime.now());
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
            saveFile(nextTime);
        }
        else this.nextTime = TaskManager.getLocalDateTime(data.getString(path));
    }
    private void calcNextTime(String str){
        LocalTime time = getRoundTime(str.split(",")[1]);
        if(time != null) this.nextTime = this.getNextTime(LocalDateTime.now().with(time));
        else this.nextTime = this.getNextTime(LocalDateTime.now());
    }
    public boolean isTimeUp(){
        LocalDateTime now = LocalDateTime.now();
        if(now.isEqual(nextTime) || now.isAfter(nextTime)){
            return true;
        }
        return false;
    }
    public TaskType getType(){
        return this.type;
    }

}
