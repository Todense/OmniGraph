package com.todense.viewmodel.layout.barnesHut;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuadTree {

    private Cell root;

    public QuadTree(int depth, Graph graph){
        Rectangle2D boundarySquare = getGraphBoundary(graph);
        Point2D center = new Point2D(boundarySquare.getMinX() + boundarySquare.getWidth()/2,
                boundarySquare.getMinY() + boundarySquare.getHeight()/2);

        root = new Cell(null, center, boundarySquare.getWidth());
        root.getNodes().addAll(graph.getNodes());
        List<Cell> cells = new ArrayList<>();
        cells.add(root);

        for (int i = 0; i < depth; i++) {
            List<Cell> newCells = new ArrayList<>();
            for (Cell cell : cells){
                cell.divide();
                newCells.addAll(Arrays.stream(cell.getChildren())
                        .filter(c -> c.getNodes().size() > 0)
                        .collect(Collectors.toList()));
            }
            cells = newCells;
        }
        root.calcCenterOfMassFromChildren();
    }

    public Rectangle2D getGraphBoundary(Graph graph){
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
        double squareLength = Math.max(xMax-xMin, yMax-yMin);
        return new Rectangle2D(xMin, yMin, squareLength, squareLength);
    }


    public Cell getRoot() {
        return root;
    }
}
