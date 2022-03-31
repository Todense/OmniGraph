package com.todense.viewmodel.layout.barnesHut;

import com.todense.model.graph.Graph;
import com.todense.util.Util;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuadTree {

    private final Cell root;

    public QuadTree(int depth, Graph graph){
        Rectangle2D boundarySquare = Util.getGraphBoundary(graph, true);
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




    public Cell getRoot() {
        return root;
    }
}
