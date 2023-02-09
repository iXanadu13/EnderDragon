package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Month extends Task {
    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusMonths(1).withDayOfMonth(day);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusMonths(1).withDayOfMonth(day);
        saveFile(nextTime);
    }
    public Month(TaskType type, String str){
        super(type,str);
    }

}
