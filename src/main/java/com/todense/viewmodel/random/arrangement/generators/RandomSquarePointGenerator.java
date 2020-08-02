package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

import java.util.Random;

public class RandomSquarePointGenerator implements Generator<Point2D> {

    double squareLength;
    Point2D center;
    Random rnd = new Random();

    public RandomSquarePointGenerator(double squareLength, Point2D center){
        this.squareLength = squareLength;
        this.center = center;
    }

    @Override
    public Point2D next() {
        return new Point2D(rnd.nextDouble(), rnd.nextDouble()).multiply(squareLength)
                .add(center.subtract(squareLength/2, squareLength/2));
    }
}
