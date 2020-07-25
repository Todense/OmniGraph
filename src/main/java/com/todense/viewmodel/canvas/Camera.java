package com.todense.viewmodel.canvas;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class Camera {

    private Affine affine;

    public Camera(){
        this.affine = new Affine();
    }

    public void zoomIn(double z, Point2D pivot){
        Point2D p = pivot;
        try {
            p = affine.inverseTransform(pivot.getX(), pivot.getY());
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        affine.appendScale(z,z, p.getX(), p.getY());
    }

    public void adjustToGraph(Graph graph,  double canvasWidth, double canvasHeight, double nodeSize){

        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double x = node.getPos().getX();
            if(x < xMin) xMin = x;
            if(x > xMax) xMax = x;

            double y = node.getPos().getY();
            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }

        Point2D center = new Point2D((xMax+xMin)/2, (yMax+yMin)/2);

        double d = Math.max((xMax-xMin+nodeSize)/(0.9 * canvasWidth), (yMax-yMin+nodeSize)/(0.9 * canvasHeight));

        affine = new Affine();

        Point2D translation = new Point2D(canvasWidth/2, canvasHeight/2).subtract(center);

        affine.appendTranslation(translation.getX(), translation.getY());
        affine.appendScale(1/d, 1/d, center);

    }

    public void translate(Point2D delta) {
        Point2D scaledDelta = delta.multiply(1/affine.getMxx());
        affine.appendTranslation(scaledDelta.getX(), scaledDelta.getY());
    }

      public Point2D inverse(Point2D point) {
        try {
            return affine.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
        }
        return new Point2D(0,0);
    }

    public Point2D transform(Point2D point){
        return affine.transform(point);
    }

    public double getZoom(){
        return affine.getMxx();
    }

    public Affine getAffine() {
        return affine;
    }
}
