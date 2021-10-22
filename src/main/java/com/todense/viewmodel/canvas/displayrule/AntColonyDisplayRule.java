package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.paint.Color;

public class AntColonyDisplayRule extends ResponsiveDisplayRule{

    private AntsScope antsScope;

    public AntColonyDisplayRule(GraphScope graphScope,BackgroundScope backgroundScope, AntsScope antsScope) {
        super(graphScope, backgroundScope);
        this.antsScope = antsScope;
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
    public double getNodeSize(Node node) {
        return super.getNodeSize(node);
    }

    @Override
    public double getEdgeWidth(Edge edge) {
        double width;
        if(antsScope.isShowingPheromones()){
            width = Math.min(
                    antsScope.getPheromone(edge) * antsScope.getScale() * antsScope.getScale(),
                    graphScope.getNodeSize() * 0.75);
        }else{
            width = 0;
        }
        return width;
    }
}
