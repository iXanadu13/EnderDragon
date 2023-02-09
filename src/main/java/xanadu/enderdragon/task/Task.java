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
            case minute, hour -> this.period = Integer.parseInt(str);
            case day -> this.period = Integer.parseInt(str.split(",")[0]);
            case week, month, year -> this.day = Integer.parseInt(str.split(",")[0]);
        }
        if(data.getString(path) == null){
            switch (type){
                case minute, hour -> this.nextTime = this.getNextTime(LocalDateTime.now());
                case day, week, month, year -> calcNextTime(str);
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
