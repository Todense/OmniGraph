package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.paint.Color;

public class ResponsiveDisplayRule extends DefaultDisplayRule{

    BackgroundScope backgroundScope;

    public ResponsiveDisplayRule(GraphScope graphScope, BackgroundScope backgroundScope) {
        super(graphScope);
        this.backgroundScope = backgroundScope;
    }

    @Override
    public Color getNodeColor(Node node) {
        Color displayColor = super.getNodeColor(node);
        if(node.isHighlighted()){
            displayColor = displayColor.brighter().brighter();
        }
        return displayColor;
    }

    @Override
    public Color getNodeBorderColor(Node node) {
        Color displayColor = super.getNodeBorderColor(node);
        if(node.isHighlighted()){
            displayColor = displayColor.brighter().brighter();
        }
        return displayColor;
    }

    @Override
    public Color getEdgeColor(Edge edge) {
        Color displayColor = super.getEdgeColor(edge);
        if(edge.isHighlighted() || edge.isSelected() || edge.isMarked()){
            displayColor = displayColor.brighter().brighter();
        }
        return displayColor;
    }

    @Override
    public double getNodeSize(Node node) {
        double size = super.getNodeSize(node);
        if(node.isHighlighted()) {
            size = size * 1.05;
        }
        if(node.isSelected()){
            size = size * 1.1;
        }
        return size;
    }

    @Override
    public double getEdgeWidth(Edge edge) {
        double width = super.getEdgeWidth(edge);
        if(edge.isHighlighted()){
            width = width * 1.5;
        }
        if(edge.isSelected()){
            width = width * 1.5;
        }
        return width;
    }
}
