package com.todense.viewmodel.algorithm;

import com.todense.viewmodel.MainViewModel;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.AlgorithmTaskScope;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;

public abstract class AlgorithmTaskManager {

    private AlgorithmTask task;
    private AlgorithmTaskScope algorithmTaskScope;
    private CanvasScope canvasScope;
    private NotificationCenter notificationCenter;

    private long startTime;


    public void initialize(AlgorithmTaskScope algorithmTaskScope, CanvasScope canvasScope, NotificationCenter notificationCenter){
        this.algorithmTaskScope = algorithmTaskScope;
        this.canvasScope = canvasScope;
        this.notificationCenter = notificationCenter;

    }

    public void startTask() {
        if (!algorithmTaskScope.isDone())
            return;

        task = null;
        try{
            task = createAlgorithmTask();
        } catch (IllegalArgumentException e){
            notificationCenter.publish(MainViewModel.WRITE, e.getMessage());
        } finally {
            if(task != null){
                setUpAndStartTask(task);
            }
        }
    }

    private void setUpAndStartTask(AlgorithmTask task){
        task.setPainter(canvasScope.getPainter());

        task.setOnSucceeded(workerStateEvent -> notificationCenter.publish(MainViewModel.ALGORITHM_FINISHED,
                task.getAlgorithmName(),
                System.currentTimeMillis() - startTime,
                task.getResultMessage()));

        task.setOnCancelled(workerStateEvent ->
                notificationCenter.publish(MainViewModel.ALGORITHM_CANCELLED, task.getAlgorithmName()));

        startTime = System.currentTimeMillis();
        notificationCenter.publish(MainViewModel.ALGORITHM_STARTED, task);
        algorithmTaskScope.startAlgorithmTask(task);
    }

    public void stopTask(){
        if(task != null && task.isRunning()){
            task.cancel();
        }
    }

    protected abstract AlgorithmTask createAlgorithmTask();

    public long getStartTime() {
        return startTime;
    }
}
