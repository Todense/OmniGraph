package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.util.Util;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.paint.Color;

public class AlgorithmDisplayRule extends ResponsiveDisplayRule{

    public AlgorithmDisplayRule(GraphScope graphScope, BackgroundScope backgroundScope) {
        super(graphScope, backgroundScope);
    }

    @Override
    public Color getNodeColor(Node node) {
        Color displayColor = super.getNodeColor(node);
        if(node.getStatus() == 0){
            displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
        }
        return displayColor;
    }

    @Override
    public Color getEdgeColor(Edge edge) {
        Color displayColor = super.getEdgeColor(edge);
        if(edge.getStatus() == 0){
            displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
        }
        else{
            displayColor = displayColor.brighter().brighter();
        }
        return displayColor;
    }


    @Override
    public double getEdgeWidth(Edge edge) {
        double multiplier = super.getEdgeWidth(edge);
        if(edge.getStatus() == 1){
            multiplier = multiplier * 2;
        }
        return multiplier;
    }
}
