package com.todense.viewmodel.canvas.displayrule;

import com.todense.model.graph.Edge;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;

public class AntColonyDisplayRule extends ResponsiveDisplayRule{

    private AntsScope antsScope;

    public AntColonyDisplayRule(GraphScope graphScope,BackgroundScope backgroundScope, AntsScope antsScope) {
        super(graphScope, backgroundScope);
        this.antsScope = antsScope;
    }


    @Override
    public double getEdgeWidth(Edge edge) {
        double width;
        if(antsScope.isShowingPheromones()){
            width = (antsScope.getPheromone(edge)/antsScope.getMaxPheromone())*graphScope.getEdgeWidth()*0.8;
        }else{
            width = 0;
        }
        return width;
    }
}
