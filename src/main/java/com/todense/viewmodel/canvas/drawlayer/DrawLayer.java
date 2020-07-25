package com.todense.viewmodel.canvas.drawlayer;

import javafx.scene.canvas.GraphicsContext;

public interface DrawLayer {

    void draw(GraphicsContext gc);

    int getOrder();

}
