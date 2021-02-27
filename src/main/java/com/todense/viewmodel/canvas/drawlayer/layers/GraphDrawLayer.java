package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.EdgeWeightMode;
import com.todense.model.NodeLabelMode;
import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.util.Util;
import com.todense.viewmodel.canvas.DisplayMode;
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

    private GraphScope graphScope;
    private BackgroundScope backgroundScope;
    private AntsScope antsScope;
    private InputScope inputScope;

    public GraphDrawLayer(GraphScope graphScope, BackgroundScope backgroundScope,
                          AntsScope antsScope, InputScope inputScope){
        this.graphScope = graphScope;
        this.backgroundScope = backgroundScope;
        this.antsScope = antsScope;
        this.inputScope = inputScope;
    }

    @Override
    public void draw(GraphicsContext gc) {
        Graph graph = graphScope.getGraphManager().getGraph();
        DisplayMode displayMode = graphScope.getDisplayMode();
        double defaultEdgeWidth = graphScope.getEdgeWidth() * graphScope.getNodeSize();
        double defaultNodeSize = graphScope.getNodeSize();
        boolean selecting = (inputScope.isSelecting() && graph.getNodes().stream().anyMatch(Node::isSelected)) ||
                !graphScope.getGraphManager().getSelectedNodes().isEmpty();
        if(graphScope.areEdgesVisibile()){
            graph.getEdges().stream().filter(e -> !isEdgePrimary(e) && e.isVisible()).forEach(e ->
                    drawEdge(e, gc, defaultEdgeWidth, displayMode, selecting));
            graph.getEdges().stream().filter(this::isEdgePrimary).forEach(e ->
                    drawEdge(e, gc, defaultEdgeWidth, displayMode, selecting));
        }

        graph.getNodes().forEach(n ->
                drawNode(n, gc, defaultNodeSize, displayMode, selecting));
    }

    private boolean isEdgePrimary(Edge e){
        return e.isSelected() || e.isMarked() || e.isHighlighted();
    }

    @Override
    public int getOrder() {
        return 3;
    }

    private void drawNode(Node node, GraphicsContext gc, double defaultSize , DisplayMode displayMode, boolean selecting){
        double size = getNodeSize(node, defaultSize, displayMode);
        Point2D pos  = graphScope.getNodePositionFunction().apply(node);
        Color color = node.getColor() != null
                ? getNodeDisplayColor(node.getColor(), node, displayMode, selecting)
                : getNodeDisplayColor(graphScope.getNodeColor(), node, displayMode, selecting);

        gc.setFill(color);
        

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

        if(graphScope.showingNodeBorder()) {
            double width = graphScope.getEdgeWidth() * graphScope.getNodeSize();
            gc.setLineWidth(width);
            gc.setStroke(getNodeDisplayColor(graphScope.getEdgeColor(), node, displayMode, selecting));
            gc.strokeOval(
                    pos.getX() - (size-width)/2,
                    pos.getY() - (size-width)/2,
                    size - width,
                    size - width
            );
        }
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

    private void drawEdge(Edge edge, GraphicsContext gc, double defaultWidth, DisplayMode displayMode, boolean selecting){
        Point2D p1 = graphScope.getNodePositionFunction().apply(edge.getN1());
        Point2D p2 = graphScope.getNodePositionFunction().apply(edge.getN2());

        double width = getEdgeWidth(edge, defaultWidth, displayMode);
        if(width == 0)
            return;

        // ant colony algorithm cycle marker
        if(edge.isMarked() && displayMode == DisplayMode.ANT_COLONY) {
            gc.setLineWidth(width + graphScope.getNodeSize() * 0.25);
            gc.setStroke(antsScope.getCycleColor());
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }


        if(displayMode == DisplayMode.ANT_COLONY && !antsScope.isShowingPheromones()) return;

        gc.setLineWidth(width);
        Color edgeColor = getEdgeDisplayColor(edge, displayMode, selecting);

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

    private Color getNodeDisplayColor(Color color, Node node, DisplayMode displayMode, boolean selecting) {
        Color displayColor = color;

        switch (displayMode){
            case DEFAULT:
                if(selecting){
                    if(!node.isSelected()){
                        displayColor = Util.getFaintColor(color, backgroundScope.getBackgroundColor());
                    }
                } else if(node.isHighlighted()){
                    displayColor = displayColor.brighter().brighter();
                }
                break;
            case ANT_COLONY:
                if(node.isHighlighted()){
                    displayColor = displayColor.brighter().brighter();
                }
                break;
            case ALGORITHMIC:
                if(!node.isMarked()){
                    displayColor = Util.getFaintColor(color, backgroundScope.getBackgroundColor());
                }
                break;
            default: displayColor = Color.PINK;
        }
        return displayColor;
    }

    private double getNodeSize(Node node, double defaultSize, DisplayMode displayMode) {

        if(node.isHighlighted()) {
            defaultSize = defaultSize * 1.05;
        }
        if(node.isSelected()){
            defaultSize = defaultSize * 1.1;
        }

        switch (displayMode){
            case DEFAULT: case ANT_COLONY:
                break;
            case ALGORITHMIC:
                if(node.isMarked()){
                    defaultSize = defaultSize * 1.05;
                }
                break;
            default: defaultSize = 0;
        }
        return defaultSize * graphScope.getNodeScaleFunction().apply(node);
    }

    private Color getEdgeDisplayColor(Edge edge, DisplayMode displayMode, boolean selecting) {
        Color displayColor = edge.getColor() != null
                ? edge.getColor()
                : graphScope.getEdgeColor();

        switch (displayMode){
            case DEFAULT: case ANT_COLONY:
                if(selecting){
                    if(!edge.isMarked()){
                        displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
                    }
                }
                else if(edge.isHighlighted() || edge.isSelected() || edge.isMarked()){
                    displayColor = displayColor.brighter().brighter();
                }
                break;
            case ALGORITHMIC:
                if(!edge.isMarked()){
                    displayColor = Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor());
                }
                break;
            default: displayColor = Color.PINK;
        }

        if(graphScope.isEdgeOpacityDecayOn()){
            double decay = getDecayedValue(edge, graphScope.getEdgeOpacityDecay(), graphScope.getNodeSize() * 2);
            if(decay > 1){
                decay = 1;
            }
            displayColor = displayColor.deriveColor(0,1,1, decay);
        }

        //return Util.getFaintColor(displayColor, backgroundScope.getBackgroundColor(), decay);
        return displayColor;
    }

    private double getEdgeWidth(Edge edge, double defaultWidth, DisplayMode displayMode) {
        switch (displayMode){
            case DEFAULT:
                if(edge.isHighlighted()){
                    defaultWidth = defaultWidth * 1.5;
                }
                if(edge.isSelected()){
                    defaultWidth = defaultWidth * 1.5;
                }
                break;
            case ALGORITHMIC:
                if(edge.isMarked()){
                    defaultWidth = defaultWidth * 2;
                }
                break;
            case ANT_COLONY:
                if(antsScope.isShowingPheromones()){
                    defaultWidth = Math.min(
                            antsScope.getPheromone(edge) * antsScope.getScale() * antsScope.getScale(),
                            graphScope.getNodeSize() * 0.75);
                }else{
                    defaultWidth = 0;
                }
                break;
        }

        if(graphScope.isEdgeWidthDecayOn()){
            double decay = getDecayedValue(edge, graphScope.getEdgeWidthDecay(), 2*graphScope.getNodeSize());
            defaultWidth *= decay;
        }

        return Math.min(defaultWidth, graphScope.getNodeSize());
    }

    private double getDecayedValue(Edge edge, double decay, double maximum){
        Point2D p1 = graphScope.getNodePositionFunction().apply(edge.getN1());
        Point2D p2 = graphScope.getNodePositionFunction().apply(edge.getN2());
        double exponent = decay*(p1.distance(p2)-maximum);
        return  2 - 2 * (Math.pow(Math.E, exponent)/(1+Math.pow(Math.E, exponent)));
    }


}
