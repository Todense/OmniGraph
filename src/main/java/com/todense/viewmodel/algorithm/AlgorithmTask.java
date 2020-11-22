package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.canvas.Painter;
import javafx.concurrent.Task;

public abstract class AlgorithmTask extends Task<Void>{

    protected Graph graph;
    protected String resultMessage = "";
    protected Painter painter;

    protected double result = 0;

    protected double startTime;
    private boolean cancelled = false;
    private boolean connectedToUI = false;

    public AlgorithmTask(Graph graph){
        this.graph = graph;
        graph.reset();
    }

    public abstract void perform() throws InterruptedException;

    protected abstract void onFinished();

    @Override
    protected Void call() throws Exception {
        startTime = System.currentTimeMillis();
        painter.startAnimationTimer();
        painter.sleep();
        perform();
        onFinished();
        return null;
    }

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

    public boolean isConnectedToUI() {
        return connectedToUI;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
