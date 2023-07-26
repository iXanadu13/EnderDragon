package pers.xanadu.enderdragon.task.type;

import pers.xanadu.enderdragon.task.Task;
import pers.xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static pers.xanadu.enderdragon.manager.TaskManager.saveFile;

public class Hour extends Task {
    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusHours(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().withSecond(0).plusHours(period);
        saveFile(nextTime);
    }
    public Hour(TaskType type, String str){
        super(type,str);
    }
}
