package com.todense.viewmodel.canvas.drawlayer;

import com.todense.viewmodel.ants.Ant;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.GraphScope;
import javafx.scene.canvas.GraphicsContext;

public class AntsDrawLayer implements DrawLayer{

    private AntsScope antsScope;
    private GraphScope graphScope;

    public AntsDrawLayer(AntsScope antsScope, GraphScope graphScope){
        this.antsScope = antsScope;
        this.graphScope = graphScope;
    }

    @Override
    public void draw(GraphicsContext gc) {
        if(graphScope.getDisplayMode() == DisplayMode.ANT_COLONY) {
            if (antsScope.isAntsAnimationOn()) {
                gc.setFill(antsScope.getAntColor());
                double size = antsScope.getAntSize() * graphScope.getNodeSize();
                for (Ant ant : antsScope.getAnts()){
                    gc.fillOval(
                            ant.getX() - size,
                            ant.getY() - size,
                            2 * size,
                            2 * size
                    );
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
