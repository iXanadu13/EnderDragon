package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Year extends Task {
    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusYears(1).withDayOfMonth(day);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusYears(1);
        saveFile(nextTime);
    }
    public Year(TaskType type, String str){
        super(type,str);
    }
}
