package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

public class Year extends Task {
    @Override
    public LocalDateTime initNextTime(LocalDateTime cur) {
        return cur.plusYears(1).withDayOfMonth(day);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusYears(1);
    }
    public Year(TaskType type, String unique_name,String world_name,String str){
        super(type,unique_name,world_name,str);
    }
}
