package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.util.Util;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.paint.Color;

public class SelectingDisplayRule extends ResponsiveDisplayRule{

    public SelectingDisplayRule(GraphScope graphScope, BackgroundScope backgroundScope) {
        super(graphScope, backgroundScope);
    }

    @Override
    public Color getNodeColor(Node node) {
        Color displayColor = super.getNodeColor(node);
        if(!node.isSelected()){
            displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
        }
        return displayColor;
    }

    @Override
    public Color getNodeBorderColor(Node node) {
        Color displayColor =  super.getNodeBorderColor(node);
        if(!node.isSelected()){
            displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
        }else{
            displayColor = displayColor.brighter().brighter();
        }
        return displayColor;
    }

    @Override
    public Color getEdgeColor(Edge edge) {
        Color displayColor = super.getEdgeColor(edge);
        if(!edge.isMarked() && !edge.isSelected()){
            displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
        }
        return displayColor;
    }

    @Override
    public Color getNodeLabelColor(Node node) {
        Color labelColor = super.getNodeLabelColor(node);

        if(!node.isSelected()){
            labelColor = Util.getFaintColor(labelColor, backgroundScope.getBackgroundColor());
        }
        return labelColor;
    }

    @Override
    public Color getEdgeWeightColor(Edge edge) {
        Color weightColor = super.getEdgeWeightColor(edge);

        if(!edge.isMarked()){
            weightColor = Util.getFaintColor(weightColor, backgroundScope.getBackgroundColor());
        }
        return weightColor;
    }
}
