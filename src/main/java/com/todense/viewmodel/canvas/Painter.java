package com.todense.viewmodel.canvas;

import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.AnimationScope;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Comparator;

public class Painter {

    private final AnimationScope animationScope;

    private final ArrayList<DrawLayer> drawLayers = new ArrayList<>();

    private boolean timerOn = false;

    private boolean animationRequest = false;

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

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (timerOn) {
                    if (animationScope.isAnimated()) {
                        draw();
                    }

                } else {
                    if (animationRequest) {
                        draw();
                        animationRequest = false;
                    }
                }
            }
        };
        animationTimer.start();
    }

    private void draw(){
        if(gc != null) {
            drawLayers.forEach((drawLayer -> drawLayer.draw(gc)));
        }
    }

    public void repaint(){
        animationRequest = true;
        if(timerOn && isAnimationOn()) return;
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

    public synchronized void startAnimationTimer(){
        //animationTimer.start();
        timerOn = true;
    }

    public synchronized void stopAnimationTimer(){
        //animationTimer.stop();
        timerOn = false;
    }

    public void pauseCheck() {
        if(!animationScope.isPaused()) return;
        synchronized (lock) {
            while (animationScope.isPaused() && !animationScope.isNextStep()) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {
                }
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
