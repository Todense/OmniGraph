package com.todense.viewmodel.algorithm;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.canvas.Painter;
import javafx.concurrent.Task;

public abstract class AlgorithmTask extends Task<Void>{

    protected static final int EDGE_LIT = 1;
    protected static final int EDGE_UNLIT = 0;
    protected static final int NODE_UNVISITED = 0;
    protected static final int NODE_VISITED = 1;

    protected String algorithmName = "Unnamed Algorithm";

    protected final Graph graph;
    protected String resultMessage = "";
    protected Painter painter;

    protected double result = 0;

    protected double startTime;
    protected boolean cancelled = false;
    private boolean connectedToUI = false;

    public AlgorithmTask(Graph graph){
        this.graph = graph;
        graph.reset();
    }

    public abstract void perform() throws InterruptedException;

    protected abstract void onFinished();

    @Override
    protected Void call(){
        startTime = System.currentTimeMillis();
        painter.startAnimationTimer();
        try{
            painter.sleep();
            perform();
        }catch (Exception e){
            if(!(e instanceof InterruptedException)){
                e.printStackTrace();
            }
        }

        onFinished();
        return null;
    }

    protected void repaint(){
        if(connectedToUI){
            painter.repaint();
        }
    }

    protected void sleep(int millis) throws InterruptedException {
        if (super.isCancelled()){
           throw new InterruptedException();
        }
        if(connectedToUI){
            painter.sleep(millis);
        }
    }

    protected void sleep() throws InterruptedException {
        if (super.isCancelled()){
            throw new InterruptedException();
        }
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

    public String getAlgorithmName() {
        return algorithmName;
    }
}
