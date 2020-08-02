package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.InputScope;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LowerDrawLayer implements DrawLayer {

    private InputScope inputScope;
    private GraphScope graphScope;

    public LowerDrawLayer(InputScope inputScope, GraphScope graphScope){
        this.inputScope = inputScope;
        this.graphScope = graphScope;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(inputScope.isConnecting()){
            drawDummyEdge(gc, graphScope.getEdgeColor().darker());
        }
    }


    private void drawDummyEdge(GraphicsContext gc, Color dummyColor){
        gc.setStroke(dummyColor);
        gc.setFill(dummyColor);
        gc.setLineWidth(graphScope.getEdgeWidth() * graphScope.getNodeSize());


        double circleSize = graphScope.getNodeSize() * 0.5;
        Point2D startPoint = inputScope.getDummyEdgeStart();
        Point2D endPoint = inputScope.getDummyEdgeEnd();

        gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        gc.fillOval(endPoint.getX()-circleSize/2, endPoint.getY()-circleSize/2, circleSize,circleSize);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
