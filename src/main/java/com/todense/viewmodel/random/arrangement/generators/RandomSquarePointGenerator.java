package com.todense.viewmodel.random.arrangement.generators;

import com.todense.viewmodel.random.Generator;
import javafx.geometry.Point2D;

import java.util.Random;

public class RandomSquarePointGenerator implements Generator<Point2D> {

    double squareLength;
    Random rnd = new Random();

    public RandomSquarePointGenerator(double squareLength){
        this.squareLength = squareLength;
    }

    @Override
    public Point2D next() {
        return new Point2D(rnd.nextDouble(), rnd.nextDouble()).multiply(squareLength)
                .subtract(squareLength/2, squareLength/2);
    }
}
