package com.todense.viewmodel.layout.barnesHut;

import com.todense.model.graph.Node;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Cell {

    private final Point2D center;
    private final double width;

    private Point2D centerOfMass;

    private List<Node> nodes = new ArrayList<>();

    private Cell[] children = new Cell[4];
    private Cell parent;

    public Cell(Cell parent, Point2D center, double width){
        this.parent = parent;
        this.center = center;
        this.width = width;
    }

    public void divide(){
        double w = width/4;

        children[0] = new Cell(this, center.add(-w, -w), 2*w);
        children[1] = new Cell(this, center.add(w, -w), 2*w);
        children[2] = new Cell(this, center.add(w, w), 2*w);
        children[3] = new Cell(this, center.add(-w, w), 2*w);

        nodes.forEach(node -> {
            if(node.getPos().getY() < center.getY()){
                if(node.getPos().getX() < center.getX())
                    children[0].getNodes().add(node);
                else
                    children[1].getNodes().add(node);

            }else{
                if(node.getPos().getX() < center.getX())
                    children[3].getNodes().add(node);
                else
                    children[2].getNodes().add(node);
            }
        });
    }

    public boolean contains(Point2D point){
        return point.getX() > center.getX() - width/2 &&
                point.getX() < center.getX() + width/2 &&
                point.getY() > center.getY() - width/2 &&
                point.getY() < center.getY() + width/2;
    }

    public void calcCenterOfMass(){
        if(nodes.size() == 0){
            centerOfMass = new Point2D(0, 0);
            return;
        }

        double centerX = nodes.stream().mapToDouble(node -> node.getPos().getX()).sum()/nodes.size();
        double centerY = nodes.stream().mapToDouble(node -> node.getPos().getY()).sum()/nodes.size();
        centerOfMass = new Point2D(centerX, centerY);
    }

    public void calcCenterOfMassFromChildren(){
        if(children[0] == null){
            calcCenterOfMass();
        }
        else{
            List<Cell> nonemptyChildren = Arrays.stream(children)
                    .filter(c -> c.getNodes().size() > 0)
                    .collect(Collectors.toList());

            Arrays.stream(children).parallel().forEach(Cell::calcCenterOfMassFromChildren);
            double x = nonemptyChildren.stream().mapToDouble(c -> c.getCenterOfMass().getX()).sum()/nonemptyChildren.size();
            double y = nonemptyChildren.stream().mapToDouble(c -> c.getCenterOfMass().getY()).sum()/nonemptyChildren.size();
            centerOfMass = new Point2D(x, y);
        }
    }

    public Point2D getCenterOfMass() {
        return centerOfMass;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public Cell[] getChildren() {
        return children;
    }

    public Cell getParent() {
        return parent;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getWidth() {
        return width;
    }
}
