package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

public class CircularPointGenerator implements Generator<Point2D> {

    private final int pointCount;
    private final double radius;

    int counter = 0;

    public CircularPointGenerator(int pointCount, double radius){
        this.pointCount = pointCount;
        this.radius = radius;
    }

    @Override
    public Point2D next() {
        double x = radius * Math.cos(-Math.PI/2 + (counter * 2 * Math.PI) / pointCount);
        double y = radius * Math.sin(-Math.PI/2 + (counter * 2 * Math.PI) / pointCount);
        counter = (counter + 1) % pointCount;
        return new Point2D(x, y);
    }
}
