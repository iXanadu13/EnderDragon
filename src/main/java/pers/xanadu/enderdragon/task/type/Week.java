package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static pers.xanadu.enderdragon.manager.TaskManager.saveFile;

public class Week extends Task {

    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.with(DayOfWeek.of(day)).plusDays(7);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusDays(7);
        saveFile(nextTime);
    }
    public Week(TaskType type, String str){
        super(type,str);
    }
}
