package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Minute extends Task {

    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.plusMinutes(period);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusMinutes(period);
        saveFile(nextTime);
    }

    public Minute(TaskType type, String str){
        super(type,str);
    }

}
