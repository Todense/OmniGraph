package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.viewmodel.canvas.Camera;
import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.CanvasScope;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class BackgroundDrawLayer implements DrawLayer {

    private final BackgroundScope backgroundScope;
    private final CanvasScope canvasScope;
    private final Camera camera;

    public BackgroundDrawLayer(BackgroundScope backgroundScope, CanvasScope canvasScope){
        this.backgroundScope = backgroundScope;
        this.canvasScope = canvasScope;
        this.camera = canvasScope.getCamera();
    }

    @Override
    public void draw(GraphicsContext gc) {

        double width = canvasScope.getCanvasWidth();
        double height = canvasScope.getCanvasHeight();

        gc.setTransform(new Affine());
        gc.setFill(backgroundScope.getBackgroundColor());
        gc.fillRect(0, 0, width, height);

        gc.setTransform(camera.getAffine());

        if(backgroundScope.isShowingGrid()){
            drawGrid(gc, width, height);
        }
    }

    private void drawGrid(GraphicsContext gc, double width, double height){

        double gap = backgroundScope.getGridGap();
        double gridWidth = backgroundScope.getGridWidth();
        double screenGap = camera.getZoom() * gap;
        double log2Floor = Math.floor(Math.log(screenGap/gap)/Math.log(2));

        if(gridWidth == 0.0) return;

        screenGap /= Math.pow(2, log2Floor);
        gap = screenGap / camera.getZoom();

        Point2D start = camera.inverse(new Point2D(0,0));
        Point2D end = camera.inverse(new Point2D(width, height));

        gc.setStroke(Color.grayRgb(backgroundScope.getGridBrightness()));
        gc.setLineWidth(gridWidth / camera.getZoom());

        //vertical lines
        int iMin = (int) (start.getX()/gap);
        int iMax = (int) (end.getX()/gap);

        for (int i = iMin; i <= iMax; i++) {
            gc.strokeLine( i*gap, start.getY(), i*gap, end.getY());
        }

        //horizontal lines
        int jMin = (int) (start.getY()/gap);
        int jMax = (int) (end.getY()/gap);

        for (int j = jMin; j <= jMax; j++) {
            gc.strokeLine(start.getX(), j*gap, end.getX(), j*gap);
        }

        //center lines
        gc.setLineWidth(2.5 * gridWidth / camera.getZoom());
        gc.strokeLine(0, start.getY(), 0, end.getY());
        gc.strokeLine(start.getX(), 0, end.getX(), 0);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
