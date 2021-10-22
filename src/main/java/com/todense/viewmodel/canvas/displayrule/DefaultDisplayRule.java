package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.paint.Color;

public class DefaultDisplayRule implements DisplayRule{

    GraphScope graphScope;

    public DefaultDisplayRule(GraphScope graphScope){
        this.graphScope = graphScope;
    }

    @Override
    public Color getNodeColor(Node node) {
        Color color = node.getColor();
        return color != null ? color : graphScope.getNodeColor();
    }

    @Override
    public Color getNodeBorderColor(Node node) {
        return graphScope.getEdgeColor();
    }

    @Override
    public Color getEdgeColor(Edge edge) {
        Color color = edge.getColor();
        return color != null ? color : graphScope.getEdgeColor();
    }

    @Override
    public double getNodeSize(Node node) {
        return graphScope.getNodeSize();
    }

    @Override
    public double getEdgeWidth(Edge edge) {
        return graphScope.getEdgeWidth();
    }
}
