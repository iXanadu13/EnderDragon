package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static pers.xanadu.enderdragon.manager.TaskManager.saveFile;

public class Year extends Task {
    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusYears(1).withDayOfMonth(day);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusYears(1);
        saveFile(nextTime);
    }
    public Year(TaskType type, String str){
        super(type,str);
    }
}
