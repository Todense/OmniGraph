package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.util.Util;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GraphDrawLayer implements DrawLayer {

    private GraphScope graphScope;
    private BackgroundScope backgroundScope;
    private AntsScope antsScope;

    public GraphDrawLayer(GraphScope graphScope, BackgroundScope backgroundScope, AntsScope antsScope){
        this.graphScope = graphScope;
        this.backgroundScope = backgroundScope;
        this.antsScope = antsScope;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Graph graph = graphScope.getGraphManager().getGraph();
        DisplayMode displayMode = graphScope.getDisplayMode();

        graph.getEdges().stream().filter(e -> !e.isMarked() && !e.isHighlighted() && e.isVisible()).forEach(e ->
                drawEdge(e, gc, displayMode));
        graph.getEdges().stream().filter(e -> e.isMarked() || e.isHighlighted()).forEach(e ->
                drawEdge(e, gc, displayMode));
        graph.getNodes().forEach(n ->
                drawNode(n, gc, displayMode));
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private void drawNode(Node node, GraphicsContext gc, DisplayMode displayMode){
        double size = getNodeSize(node, displayMode);
        Color color = getNodeColor(node.getColor(), node, displayMode);

        gc.setFill(color);

        if(graphScope.showingNodeBorder()){

            double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
            gc.fillOval(node.getPos().getX() - (size-width)/2,
                    node.getPos().getY() - (size-width)/2,
                    size-width,
                    size-width
            );
        }else{
            gc.fillOval(node.getPos().getX() - size/2, node.getPos().getY() - size/2, size, size);
        }

        if(graphScope.showingNodeBorder()) {
            double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
            gc.setLineWidth(width);
            gc.setStroke(getNodeColor(graphScope.getEdgeColor(), node, displayMode));
            gc.strokeOval(
                    node.getPos().getX() - (size-width)/2,
                    node.getPos().getY() - (size-width)/2,
                    size - width,
                    size - width
            );
        }

        if(node.isSelected()){
            gc.setStroke(backgroundScope.getBackgroundColor().invert());
            gc.setLineWidth(graphScope.getNodeSize() * 0.05);
            double circleSize = graphScope.getNodeSize() * 1.5;
            gc.strokeOval(node.getPos().getX() - circleSize/2, node.getPos().getY() - circleSize/2, circleSize, circleSize);
        }

        drawNodeLabel(node, gc);

    }

    private void drawNodeLabel(Node node, GraphicsContext gc) {
        double size = graphScope.getNodeSize();

        gc.setFill(graphScope.getNodeLabelColor());
        gc.setFont(new Font("Computer Modern", graphScope.getNodeSize() * 0.6));
        gc.setTextAlign(TextAlignment.CENTER);

        String s = "";

        if(graphScope.getNodeLabelMode() == NodeLabelMode.NUMBER) {
            s = String.valueOf(node.getIndex()+1);
        }
        else if(graphScope.getNodeLabelMode() == NodeLabelMode.CUSTOM){
            s = node.getLabelText();
        }
        gc.fillText(s, node.getPos().getX(), node.getPos().getY()+size/5, size*2);
    }

    private void drawEdge(Edge edge, GraphicsContext gc, DisplayMode displayMode){
        Point2D p1 = edge.getN1().getPos();
        Point2D p2 = edge.getN2().getPos();

        double width = getEdgeWidth(edge, displayMode);

        //makes line shorter when it is wider
        Point2D correctionVector = p1.subtract(p2).normalize().multiply(width/2);

        if(edge.isMarked() && displayMode == DisplayMode.ANT_COLONY) {
            gc.setLineWidth(width + graphScope.getNodeSize() * 0.25);
            gc.setStroke(antsScope.getCycleColor());
            gc.strokeLine(p1.getX() - correctionVector.getX(),
                    p1.getY() - correctionVector.getY(),
                    p2.getX() + correctionVector.getX(),
                    p2.getY() + correctionVector.getY());
        }

        if(displayMode == DisplayMode.ANT_COLONY && !antsScope.isShowingPheromones()) return;

        gc.setLineWidth(width);
        gc.setStroke(getEdgeColor(edge, displayMode));

        gc.strokeLine(p1.getX() - correctionVector.getX(),
                p1.getY() - correctionVector.getY(),
                p2.getX() + correctionVector.getX(),
                p2.getY() + correctionVector.getY());


        if(graphScope.getEdgeWeightMode() != EdgeWeightMode.NONE){
            if(graphScope.getEdgeWeightMode() == EdgeWeightMode.LENGTH){
                drawEdgeWeight(edge, gc, String.valueOf((int)(edge.getN1().getPos().distance(edge.getN2().getPos()))), width * 3);
            }
            else if(graphScope.getEdgeWeightMode() == EdgeWeightMode.CUSTOM){
                double weight = edge.getWeight();
                if((weight % 1) == 0){
                    drawEdgeWeight(edge, gc, String.valueOf((int)weight), width * 3);
                }else {
                    drawEdgeWeight(edge, gc, String.valueOf(weight), width * 3);
                }
            }
        }
    }

    private void drawEdgeWeight(Edge e, GraphicsContext gc, String weight, double fontSize) {
        Point2D midPoint = e.getN1().getPos().midpoint(e.getN2().getPos());
        gc.setFont(new Font("Computer Modern", fontSize));
        gc.setFill(graphScope.getEdgeWeightColor());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(weight, midPoint.getX(), midPoint.getY() + fontSize/3);
    }

    private Color getNodeColor(Color color, Node node, DisplayMode displayMode) {
        Color newColor = color;

        if(node.isSelected()){
            newColor = newColor.brighter().brighter();
        }

        switch (displayMode){
            case DEFAULT: case ANT_COLONY:
                if(node.isHighlighted()){
                    newColor = newColor.brighter().brighter();
                }
                break;
            case ALGORITHMIC:
                if(!node.isMarked()){
                    newColor = Util.getFaintColor(color, backgroundScope.getBackgroundColor());
                }
                break;
            default: newColor = Color.PINK;
        }
        return newColor;
    }

    private double getNodeSize(Node node, DisplayMode displayMode) {
        double size = graphScope.getNodeSize();

        if(node.isHighlighted()) {
            size = size * 1.05;
        }
        if(node.isSelected()){
            size = size * 1.1;
        }

        switch (displayMode){
            case DEFAULT: case ANT_COLONY:
                break;
            case ALGORITHMIC:
                if(node.isMarked()){
                    size = size * 1.05;
                }
                break;
            default: size = 0;
        }
        return size;
    }

    private Color getEdgeColor(Edge edge, DisplayMode displayMode) {
        Color color;
        switch (displayMode){
            case DEFAULT: case ANT_COLONY:
                if(edge.isHighlighted() || edge.isSelected()){
                    color = edge.getColor().brighter().brighter();
                }else{
                    color = edge.getColor();
                } break;
            case ALGORITHMIC:
                if(edge.isMarked()){
                    color = edge.getColor();
                }else{
                    color = Util.getFaintColor(edge.getColor(), backgroundScope.getBackgroundColor());
                } break;
            default: color = Color.PINK;
        }
        return color;
    }

    private double getEdgeWidth(Edge edge, DisplayMode displayMode) {
        double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
        switch (displayMode){
            case DEFAULT:
                if(edge.isHighlighted()){
                    width = width * 1.5;
                }
                if(edge.isSelected()){
                    width = width * 1.5;
                }
                break;
            case ALGORITHMIC:
                if(edge.isMarked()){
                    width = width * 2;
                }
                break;
            case ANT_COLONY:
                if(antsScope.isShowingPheromones()){
                    width = Math.min(antsScope.getPheromone(edge) * antsScope.getScale() * antsScope.getScale(), graphScope.getNodeSize() * 0.75);
                }else{
                    width = 0;
                }
                break;
        }
        return width;
    }
}
