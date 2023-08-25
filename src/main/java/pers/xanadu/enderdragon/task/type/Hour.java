package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

public class Hour extends Task {
    @Override
    public LocalDateTime initNextTime(LocalDateTime cur) {
        return cur.plusHours(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusHours(period);
    }
    public Hour(TaskType type, String unique_name,String world_name,String str){
        super(type,unique_name,world_name,str);
    }
}
