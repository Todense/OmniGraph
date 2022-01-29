package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.WalkingAgent;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class UpperDrawLayer implements DrawLayer {

    private final GraphScope graphScope;
    private final InputScope inputScope;
    private final BackgroundScope backgroundScope;
    private final AlgorithmScope algorithmScope;
    private final AntsScope antsScope;

    public UpperDrawLayer(GraphScope graphScope,
                          InputScope inputScope,
                          BackgroundScope backgroundScope,
                          AlgorithmScope algorithmScope,
                          AntsScope antsScope){
        this.graphScope = graphScope;
        this.inputScope = inputScope;
        this.backgroundScope = backgroundScope;
        this.algorithmScope = algorithmScope;
        this.antsScope = antsScope;
    }


    @Override
    public void draw(GraphicsContext gc) {

        //start & goal node markers
        if(graphScope.getDisplayMode() != DisplayMode.ANT_COLONY && algorithmScope.isShowingEndpoints()) {

            double size = graphScope.getNodeSize() * 0.5;
            double triangleSize = size * 0.9;
            double lineWidth = graphScope.getNodeSize() * 0.15;

            Node startNode = algorithmScope.getStartNode();
            if(startNode != null){
                //if start node is not in current graph, set it to null
                if(!graphScope.getGraphManager().getGraph().getNodes().contains(startNode)){
                    algorithmScope.setStartNode(null);
                }
            }
            startNode = algorithmScope.getStartNode();

            if (algorithmScope.getAlgorithm().isWithStart() && startNode != null) {
                drawMarker(gc, startNode, lineWidth, triangleSize, -1);
            }

            Node goalNode = algorithmScope.getGoalNode();
            if(goalNode != null) {
                //if goal node is not in current graph, set it to null
                if (!graphScope.getGraphManager().getGraph().getNodes().contains(goalNode)) {
                    algorithmScope.setGoalNode(null);
                }
            }
            goalNode = algorithmScope.getGoalNode();

            if (algorithmScope.getAlgorithm().isWithGoal() && goalNode != null) {
                drawMarker(gc, goalNode, lineWidth, triangleSize, 1);
            }
        }

        if(graphScope.getDisplayMode() == DisplayMode.ANT_COLONY){
            if (antsScope.isAntsAnimationOn()) {
                gc.setFill(antsScope.getAntColor());
                double size = antsScope.getAntSize() * graphScope.getNodeSize();
                for(WalkingAgent agent: algorithmScope.getWalkingAgents()) {
                    gc.fillOval(
                            agent.getX() - size,
                            agent.getY() - size,
                            2 * size,
                            2 * size
                    );
                }
            }
        }

        gc.setTransform(new Affine());

        //select rectangle
        if (inputScope.isSelecting()) {
            drawSelectRect(gc);
        }
    }


    private void drawMarker(GraphicsContext gc, Node node, double lineWidth, double triangleSize, int dir) {
        //marker's border
        Point2D pos = node.getPos();
        gc.setStroke(backgroundScope.getBackgroundColor().invert().grayscale());
        gc.setFill(backgroundScope.getBackgroundColor().invert().grayscale());
        double size = graphScope.getNodeSize() + lineWidth;
        gc.setLineWidth(lineWidth);
        gc.strokeOval(pos.getX() - size/2, pos.getY() - size/2, size, size);

        //marker's triangle
        Point2D pt = node.getPos().add(new Point2D(0, dir).multiply(size/2 + lineWidth * 0.9));
        double[] x = new double[]{pt.getX(), pt.getX() - triangleSize, pt.getX() + triangleSize};
        double[] y = new double[]{pt.getY(), pt.getY() + dir * triangleSize, pt.getY() + dir * triangleSize};
        gc.fillPolygon(x, y, 3);
    }

    private void drawSelectRect(GraphicsContext gc){
        Color rectColor = backgroundScope.getBackgroundColor()
                .invert()
                .grayscale()
                .deriveColor(0,1,1,0.2);
        Rectangle2D rect = inputScope.getSelectRect();

        gc.setFill(rectColor);
        gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
        gc.setStroke(rectColor.darker().darker());
        gc.setLineWidth(0.3);
        gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
