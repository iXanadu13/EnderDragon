package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Week extends Task {

    @Override
    public LocalDateTime initNextTime(LocalDateTime cur) {
        return cur.with(DayOfWeek.of(day)).plusDays(7);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusDays(7);
    }
    public Week(TaskType type, String unique_name,String world_name,String str){
        super(type,unique_name,world_name,str);
    }
}
