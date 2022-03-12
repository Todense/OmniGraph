package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.GraphScope;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class DefaultDisplayRule implements DisplayRule{

    final GraphScope graphScope;

    public DefaultDisplayRule(GraphScope graphScope){
        this.graphScope = graphScope;
    }

    @Override
    public Color getNodeColor(Node node) {
        Color color = node.getColor();
        return color != null ? color : getDefaultNodeColor();
    }

    @Override
    public Color getNodeBorderColor(Node node) {
        return graphScope.getEdgeColor();
    }

    @Override
    public Color getEdgeColor(Edge edge) {
        Color color = edge.getColor();
        if(color == null){
            color = getDefaultEdgeColor();
        }
        if(graphScope.isEdgeOpacityDecayOn()){
            double decay = getDecayedValue(edge, graphScope.getEdgeOpacityDecay(), graphScope.getNodeSize() * 2);
            if(decay > 1){
                decay = 1;
            }
            color = color.deriveColor(0,1,1, decay);
        }
        return color;
    }

    @Override
    public Color getNodeLabelColor(Node node) {
        return graphScope.getNodeLabelColor();
    }

    @Override
    public Color getEdgeWeightColor(Edge edge) {
        return graphScope.getEdgeWeightColor();
    }

    @Override
    public double getNodeSize(Node node) {
        return graphScope.getNodeSize();
    }

    @Override
    public double getEdgeWidth(Edge edge) {
        double width = graphScope.getEdgeWidth();
        if(graphScope.isEdgeWidthDecayOn()){
            double decay = getDecayedValue(edge, graphScope.getEdgeWidthDecay(), 2*graphScope.getNodeSize());
            width *= decay;
        }
        return width;
    }

    private double getDecayedValue(Edge edge, double decay, double maximum){
        Point2D p1 = edge.getN1().getPos();
        Point2D p2 = edge.getN2().getPos();
        double exponent = decay*(p1.distance(p2)-maximum);
        return  2 - 2 * (Math.pow(Math.E, exponent)/(1+Math.pow(Math.E, exponent)));
    }

    protected final Color getDefaultNodeColor(){
        return graphScope.getNodeColor();
    }

    protected final Color getDefaultEdgeColor(){
        return graphScope.getEdgeColor();
    }
}
