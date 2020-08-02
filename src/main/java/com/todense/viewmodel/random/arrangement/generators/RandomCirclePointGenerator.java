package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

import java.util.Random;


public class RandomCirclePointGenerator implements Generator<Point2D> {

    double radius;
    Point2D center;
    Random rnd = new Random();

    public RandomCirclePointGenerator(double radius, Point2D center){
        this.radius = radius;
        this.center = center;
    }

    @Override
    public Point2D next() {
        double r = Math.sqrt(rnd.nextDouble()) * radius;
        double angle = rnd.nextDouble() * Math.PI * 2;
        return new Point2D(r * Math.cos(angle), r * Math.sin(angle)).add(center);
    }
}
