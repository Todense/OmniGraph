package com.todense.viewmodel.scope;

import com.todense.viewmodel.algorithm.AlgorithmTask;
import de.saxsys.mvvmfx.Scope;

public class TaskScope implements Scope {

    private AlgorithmTask task;

    private Thread thread;

    public AlgorithmTask getTask() {
        return task;
    }

    public void setTask(AlgorithmTask task) {
        if(task != null && task.isRunning()){
            task.cancel();
        }
        this.task = task;
    }

    public void setThread(Thread thread){
        if(this.thread != null && this.thread.isAlive()){
            this.thread.interrupt();
        }
        this.thread = thread;
    }

    public void stopTask(){
        if(task != null && task.isRunning()){
            task.cancel();
        }
    }

    public void stopThread(){
        if(thread != null && thread.isAlive()){
            thread.interrupt();
            System.out.println(thread.getState());
        }
    }

    public void stop(){
        stopTask();
        stopThread();
    }

}
