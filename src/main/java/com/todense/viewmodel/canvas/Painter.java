package com.todense.viewmodel.canvas;

import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.AnimationScope;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;

public class Painter {

    private AnimationScope animationScope;

    private ArrayList<DrawLayer> drawLayers = new ArrayList<>();

    private boolean timerOn = false;

    private AnimationTimer animationTimer;

    final Object lock = new Object();

    private GraphicsContext gc;

    public Painter(AnimationScope animationScope) {
        this.animationScope = animationScope;

        this.animationScope.pausedProperty().addListener((obs, oldVal, newVal) -> {
            synchronized (lock) {
                lock.notifyAll();
            }
        });
        this.animationScope.nextStepProperty().addListener((obs, oldVal, newVal) -> {
            synchronized (lock) {
                lock.notifyAll();
            }
        });

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if(animationScope.isAnimated()) {
                    draw();
                }
            }
        };
    }

    private void draw(){
        drawLayers.forEach((drawLayer -> drawLayer.draw(gc)));
    }

    public void repaint(){
        if(timerOn) return;

        Platform.runLater(this::draw);
    }


    public void sleep(int millis) throws InterruptedException {
        if(animationScope.isAnimated()){
            Thread.sleep(millis);
        }
        pauseCheck();
    }


    public void sleep() throws InterruptedException {
        sleep(animationScope.getStepTime());
    }

    public void startAnimationTimer(){
        timerOn = true;
        animationTimer.start();
    }

    public void stopAnimationTimer(){
        timerOn = false;
        animationTimer.stop();
    }

    public void pauseCheck() throws InterruptedException {
        if(!animationScope.isPaused()) return;
        synchronized (lock) {
            while (animationScope.isPaused() && !animationScope.isNextStep()) {
                lock.wait();
            }
            animationScope.nextStepProperty().setValue(false);
        }
    }

    public void addDrawLayer(DrawLayer drawLayer) {
        drawLayers.add(drawLayer);
        drawLayers.sort(Comparator.comparingInt(DrawLayer::getOrder));
    }

    public void setGraphicContext(GraphicsContext gc) {
        this.gc = gc;
    }

    public int getStepTime(){
        return animationScope.getStepTime();
    }

    public boolean isAnimationOn(){
        return animationScope.isAnimated();
    }
}
