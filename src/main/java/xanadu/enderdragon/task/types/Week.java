package xanadu.enderdragon.task.types;

import xanadu.enderdragon.task.Task;
import xanadu.enderdragon.task.TaskType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static xanadu.enderdragon.manager.TaskManager.saveFile;

public class Week extends Task {

    @Override
    public LocalDateTime getNextTime(LocalDateTime cur) {
        return cur.with(DayOfWeek.of(day)).plusDays(7);
    }
    @Override
    public void updateTime(){
        this.nextTime = LocalDateTime.now().plusDays(7);
        saveFile(nextTime);
    }
    public Week(TaskType type, String str){
        super(type,str);
    }
}
