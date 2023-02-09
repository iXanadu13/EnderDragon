package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Hour extends Task {
    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusHours(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusHours(period);
        saveFile(nextTime);
    }
    public Hour(TaskType type, String str){
        super(type,str);
    }
}
