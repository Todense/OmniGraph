package com.todense.viewmodel.scope;

import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.layout.task.AutoD3LayoutTask;
import de.saxsys.mvvmfx.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskScope implements Scope {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> future;
    private AlgorithmTask task;

    public boolean start(AlgorithmTask task){
        if(isDone()){
            this.executor = Executors.newSingleThreadExecutor();
            this.future = executor.submit(task);
            this.task = task;
            return true;
        }
        return false;
    }

    public boolean isDone(){
        return this.future == null || this.future.isCancelled() || this.future.isDone();
    }

    public void stopTask(){
        if(task != null && task.isRunning() && !(task instanceof AutoD3LayoutTask)){
            task.cancel();
        }
    }

    public AlgorithmTask getTask() {
        return task;
    }


}
