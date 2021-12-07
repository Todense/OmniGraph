package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

import java.util.Random;


public class RandomCirclePointGenerator implements Generator<Point2D> {

    double radius;
    Random rnd = new Random();

    public RandomCirclePointGenerator(double radius){
        this.radius = radius;
    }

    @Override
    public Point2D next() {
        double r = Math.sqrt(rnd.nextDouble()) * radius;
        double angle = rnd.nextDouble() * Math.PI * 2;
        return new Point2D(r * Math.cos(angle), r * Math.sin(angle));
    }
}
