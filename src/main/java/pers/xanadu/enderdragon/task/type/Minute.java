package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

public class Minute extends Task {

    @Override
    public LocalDateTime initNextTime(LocalDateTime cur) {
        return cur.plusMinutes(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusMinutes(period);
    }

    public Minute(TaskType type, String unique_name,String world_name,String str){
        super(type,unique_name,world_name,str);
    }
}
