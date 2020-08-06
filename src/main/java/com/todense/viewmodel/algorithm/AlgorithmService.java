package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.canvas.Painter;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public abstract class AlgorithmService extends Service<Void> {

    protected Graph graph;
    protected String resultMessage = "";
    protected Painter painter;

    protected double result = 0;

    protected double startTime;
    private boolean cancelled = false;
    private boolean connectedToUI = false;

    public AlgorithmService(Graph graph){
        this.graph = graph;
        graph.reset();
    }

    public abstract void perform() throws InterruptedException;

    protected abstract void onFinished();

    protected void repaint(){
        if(connectedToUI){
            painter.repaint();
        }
    }

    protected void sleep() throws InterruptedException {
        if(connectedToUI){
            painter.sleep();
        }
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws InterruptedException {
                startTime = System.currentTimeMillis();
                painter.startAnimationTimer();
                painter.sleep();
                perform();
                onFinished();
                return null;
            }

            @Override
            protected void succeeded() {
                painter.stopAnimationTimer();
                painter.repaint();
            }

            @Override
            protected void cancelled() {
                cancelled = true;
                painter.stopAnimationTimer();
                painter.repaint();
            }
        };
    }

    public double getStartTime() {
        return startTime;
    }

    public void setPainter(Painter painter){
        this.painter = painter;
        connectedToUI = true;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
