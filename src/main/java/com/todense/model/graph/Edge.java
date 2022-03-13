package com.todense.model.graph;

import javafx.scene.paint.Color;

public class Edge {

    public static Color DEFAULT_COLOR = Color.rgb(120,160,200);

    private final String id;

    private final Node n1;
    private final Node n2;

    private Color color;

    double weight = 1d;
    double length = 0;

    private boolean marked = false;
    private boolean selected = false;
    private boolean visible = true;
    private boolean highlighted = false;
    private int status = 0;

    protected Edge(Node n1, Node n2) {
        if(n1.equals(n2)){
            throw new IllegalArgumentException("Cannot create loop");
        }
        this.n1 = n1;
        this.n2 = n2;
        id = n1.getID() < n2.getID() ?
                n1.getID()+"-"+n2.getID() :
                n2.getID()+"-"+n1.getID();
        this.color = Edge.DEFAULT_COLOR;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public double getLength() {
        return length;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public double calcLength() {
        this.length = n1.getPos().distance(n2.getPos());
        return this.length;
    }

    public Node getN1() {
        return n1;
    }

    public Node getN2() {
        return n2;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return marked;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String toString(){
        return id;
    }

    public String getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}