package com.todense.model.graph;


import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Node{

    private int index;
    private int id;
    private String labelText="";
    private Graph g;
    private Point2D pos;
    private Color color;

    private boolean visited = false;
    private boolean selected = false;
    private boolean highlighted = false;

    private ArrayList<Node> neighbours = new ArrayList<>();

    public Node(Point2D pos, Graph g) {
        this.pos = pos;
        this.g = g;
        this.color = Color.rgb(50,240,45);
        this.index = g.getNodes().size();
        this.id = g.idCounter++;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public boolean isVisited(){return this.visited;}

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setVisited(boolean visited){this.visited = visited;}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Graph getGraph() {
        return g;
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

    public int getId() {
        return id;
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
}
