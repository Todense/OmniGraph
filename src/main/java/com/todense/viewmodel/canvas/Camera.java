package com.todense.viewmodel.canvas;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.graph.GraphAnalyzer;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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

        if(graph.getNodes().size() == 0) return;

        Rectangle2D graphBounds = GraphAnalyzer.getGraphBounds(graph);

        Point2D center = new Point2D(graphBounds.getMinX()+graphBounds.getWidth()/2,
                graphBounds.getMinY()+graphBounds.getHeight()/2);

        double d = Math.max((graphBounds.getMaxX()-graphBounds.getMinX()+nodeSize)/(0.9 * canvasWidth),
                (graphBounds.getMaxY()-graphBounds.getMinY()+nodeSize)/(0.9 * canvasHeight));

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
