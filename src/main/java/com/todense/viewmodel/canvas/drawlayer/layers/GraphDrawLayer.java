package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.ants.AntColonyAlgorithmTask;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.displayrule.*;
import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.AntsScope;
import com.todense.viewmodel.scope.BackgroundScope;
import com.todense.viewmodel.scope.GraphScope;
import com.todense.viewmodel.scope.InputScope;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GraphDrawLayer implements DrawLayer {

    private final GraphScope graphScope;
    private final BackgroundScope backgroundScope;
    private final AntsScope antsScope;
    private final InputScope inputScope;

    public GraphDrawLayer(GraphScope graphScope, BackgroundScope backgroundScope,
                          AntsScope antsScope, InputScope inputScope){
        this.graphScope = graphScope;
        this.backgroundScope = backgroundScope;
        this.antsScope = antsScope;
        this.inputScope = inputScope;
    }

    public DisplayRule getDisplayRule(DisplayMode displayMode){
        DisplayRule displayRule = null;
        switch (displayMode){
            case DEFAULT: {
                Graph graph = graphScope.getGraphManager().getGraph();
                boolean selecting = (inputScope.isSelecting() && graph.getNodes().stream().anyMatch(Node::isSelected)) ||
                        !graphScope.getGraphManager().getSelectedNodes().isEmpty();
                if(selecting){
                    displayRule = new SelectingDisplayRule(graphScope, backgroundScope); break;
                }else{
                    displayRule = new ResponsiveDisplayRule(graphScope, backgroundScope);
                }
                break;
            }
            case ALGORITHMIC: displayRule = new AlgorithmDisplayRule(graphScope, backgroundScope); break;
            case ANT_COLONY: displayRule = new AntColonyDisplayRule(graphScope, backgroundScope, antsScope); break;
        }
        return displayRule;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Graph graph = graphScope.getGraphManager().getGraph();
        DisplayMode displayMode = graphScope.getDisplayMode();
        DisplayRule displayRule = this.getDisplayRule(displayMode);

        synchronized (Graph.LOCK) {
            if (graphScope.areEdgesVisibile()) {
                graph.getEdges().stream().filter(e -> e != null && !isEdgePrimary(e) && e.isVisible()).forEach(e ->
                        drawEdge(e, gc, displayRule));
                graph.getEdges().stream().filter(e -> e != null && isEdgePrimary(e) && e.isVisible()).forEach(e ->
                        drawEdge(e, gc, displayRule));
            }

            if(inputScope.isConnecting()){
                drawDummyEdge(gc, graphScope.getEdgeColor().darker());
            }

            graph.getNodes().forEach(n ->
                    drawNode(n, gc, displayRule));
        }
    }

    private boolean isEdgePrimary(Edge e){
        return e.isSelected() || e.isMarked() || e.isHighlighted() || e.getStatus() > 0;
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private void drawNode(Node node, GraphicsContext gc, DisplayRule displayRule){
        double size = displayRule.getNodeSize(node);
        Point2D pos  = node.getPos();
        Color color = displayRule.getNodeColor(node);

        gc.setFill(color);

        // interior
        if(graphScope.showingNodeBorder()){
            double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
            gc.fillOval(pos.getX() - (size-width)/2,
                    pos.getY() - (size-width)/2,
                    size-width,
                    size-width
            );
        }else{
            gc.fillOval(pos.getX() - size/2, pos.getY() - size/2, size, size);
        }

        //border
        if(graphScope.showingNodeBorder()) {
            double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
            gc.setLineWidth(width);
            gc.setStroke(displayRule.getNodeBorderColor(node));
            gc.strokeOval(
                    pos.getX() - (size-width)/2,
                    pos.getY() - (size-width)/2,
                    size - width,
                    size - width
            );
        }

        //pin
        if(node.isPinned()){
            Color pinColor;
            if(color.getBrightness() == 0.0){
                pinColor = Color.GRAY;
            }else{
                pinColor = color.getBrightness() > 0.5 ?
                        color.deriveColor(0,1,1-0.3/color.getBrightness(),1) :
                        color.deriveColor(0,1,1+0.3/color.getBrightness(),1);
            }
            gc.setFill(pinColor);
            gc.fillOval(pos.getX() - size/6, pos.getY() - size/6, size/3, size/3);
        }

        //label
        if(!graphScope.nodeLabelModeProperty().get().equals(NodeLabelMode.NONE)){
            drawNodeLabel(node, gc);
        }
    }

    private void drawNodeLabel(Node node, GraphicsContext gc) {
        double size = graphScope.getNodeSize();

        gc.setFill(graphScope.getNodeLabelColor());
        gc.setFont(new Font("Sans Serif", graphScope.getNodeSize() * 0.6));
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

    private void drawEdge(Edge edge, GraphicsContext gc, DisplayRule displayRule){
        Point2D p1 = edge.getN1().getPos();
        Point2D p2 = edge.getN2().getPos();

        double width = displayRule.getEdgeWidth(edge) * graphScope.getNodeSize();
        width = Math.min(width, graphScope.getNodeSize());

        // ant colony algorithm cycle marker
        if(edge.getStatus() == AntColonyAlgorithmTask.EDGE_ON_CYCLE && displayRule.getClass().equals(AntColonyDisplayRule.class)) {
            gc.setLineWidth(width + graphScope.getNodeSize() * 0.2);
            gc.setStroke(antsScope.getCycleColor());
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }

        if(width == 0)
            return;

        if(displayRule.getClass().equals(AntColonyDisplayRule.class)  && !antsScope.isShowingPheromones()) return;

        gc.setLineWidth(width);

        Color edgeColor = displayRule.getEdgeColor(edge);

        if(edgeColor.equals(backgroundScope.getBackgroundColor())){
            return;
        }
        gc.setStroke(edgeColor);

        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());

        if(graphScope.getEdgeWeightMode() != EdgeWeightMode.NONE){
            if(graphScope.getEdgeWeightMode() == EdgeWeightMode.LENGTH){
                drawEdgeWeight(edge, gc, String.valueOf((int)(edge.calcLength())), width * 3);
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
        gc.setFont(new Font("Sans Serif", fontSize));
        gc.setFill(graphScope.getEdgeWeightColor());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(weight, midPoint.getX(), midPoint.getY() + fontSize/3);
    }

    private void drawDummyEdge(GraphicsContext gc, Color dummyColor){
        gc.setStroke(dummyColor);
        gc.setFill(dummyColor);
        gc.setLineWidth(graphScope.getEdgeWidth() * graphScope.getNodeSize());

        double circleSize = graphScope.getNodeSize() * 0.5;
        Point2D endPoint = inputScope.getDummyEdgeEnd();
        for(Node node : inputScope.getDummyEdgeStartNodes()){
            Point2D startPoint = node.getPos();
            gc.strokeLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        }
        gc.fillOval(endPoint.getX()-circleSize/2, endPoint.getY()-circleSize/2, circleSize,circleSize);
    }


}
