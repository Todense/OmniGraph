package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

public class CircularPointGenerator implements Generator<Point2D> {

    private final int pointCount;
    private final double radius;
    private final Point2D center;

    int counter = 0;

    public CircularPointGenerator(int pointCount, double radius, Point2D center){
        this.pointCount = pointCount;
        this.radius = radius;
        this.center = center;
    }

    @Override
    public Point2D next() {
        double x = center.getX() + radius * Math.cos(-Math.PI/2 + (counter * 2 * Math.PI) / pointCount);
        double y = center.getY() + radius * Math.sin(-Math.PI/2 + (counter * 2 * Math.PI) / pointCount);
        counter = (counter + 1) % pointCount;
        return new Point2D(x, y);
    }
}
