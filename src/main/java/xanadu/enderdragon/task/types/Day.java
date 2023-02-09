package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Day extends Task {

    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusDays(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusDays(period);
        saveFile(nextTime);
    }
    public Day(TaskType type, String str){
        super(type,str);
    }
}
