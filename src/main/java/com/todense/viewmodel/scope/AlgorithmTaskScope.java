package com.todense.viewmodel.scope;

import com.todense.viewmodel.algorithm.AlgorithmTask;
import de.saxsys.mvvmfx.Scope;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AlgorithmTaskScope implements Scope {

    private Future<?> future;
    private AlgorithmTask algorithmTask;

    public void startAlgorithmTask(AlgorithmTask task){
        if(isDone()){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            this.future = executor.submit(task);
            this.algorithmTask = task;
        }
    }

    public boolean isDone(){
        return this.future == null || this.future.isCancelled() || this.future.isDone();
    }

    public void stopAlgorithmTask(){
        if(algorithmTask != null && algorithmTask.isRunning()){
            algorithmTask.cancel();
        }
    }

    public AlgorithmTask getAlgorithmTask() {
        return algorithmTask;
    }


}
