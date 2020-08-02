package com.todense.model.graph;


import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Node{

    private final int ID;

    private int index;
    private String labelText="";
    private Graph graph;
    private Point2D pos;
    private Color color;

    private boolean marked = false; //flag changing visuals
    private boolean visited = false; //flag not changing visuals
    private boolean selected = false; //selected by user
    private boolean highlighted = false; //highlighted by user

    private ArrayList<Node> neighbours = new ArrayList<>();

    public Node(Point2D pos, Graph g) {
        this.pos = pos;
        this.graph = g;
        this.color = Color.rgb(50,240,45);
        this.index = g.getNodes().size();
        this.ID = g.nextID();
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public boolean isMarked(){return this.marked;}

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setMarked(boolean marked){this.marked = marked;}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    public Point2D getPos() {
        return pos;
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getID() {
        return ID;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String toString(){
        return String.valueOf(index+1);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}
