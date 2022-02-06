package com.todense.model.graph;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Node{

    public static Color DEFAULT_COLOR = Color.rgb(50,90,170);

    private final int ID;

    private int index;
    private String labelText="";
    private Point2D pos;
    private Color color;

    private boolean selected = false; //selected by user
    private boolean highlighted = false; //highlighted by user
    private boolean pinned = false; // position locked
    private boolean dragged = false;
    private int status = 0;

    private final ArrayList<Node> neighbours = new ArrayList<>();

    protected Node(Point2D pos, int index, int id) {
        this.pos = pos;
        this.index = index;
        this.ID = id;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public int getID() {
        return ID;
    }

    public int getDegree(){
        return neighbours.size();
    }

    protected void setIndex(int i) {
        this.index = i;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color == null ? DEFAULT_COLOR : color;
    }

    public String toString(){
        return "n"+ (index + 1);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isDragged() {
        return dragged;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }
}
