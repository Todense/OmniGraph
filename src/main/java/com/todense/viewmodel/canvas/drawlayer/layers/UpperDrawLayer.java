package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.graph.Node;
import com.todense.viewmodel.canvas.DisplayMode;
import com.todense.viewmodel.canvas.drawlayer.DrawLayer;
import com.todense.viewmodel.scope.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;

public class UpperDrawLayer implements DrawLayer {

    private GraphScope graphScope;
    private InputScope inputScope;
    private CanvasScope canvasScope;
    private BackgroundScope backgroundScope;
    private AlgorithmScope algorithmScope;

    public UpperDrawLayer(GraphScope graphScope,
                          InputScope inputScope,
                          CanvasScope canvasScope,
                          BackgroundScope backgroundScope,
                          AlgorithmScope algorithmScope){
        this.graphScope = graphScope;
        this.inputScope = inputScope;
        this.canvasScope = canvasScope;
        this.backgroundScope = backgroundScope;
        this.algorithmScope = algorithmScope;
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

            /*
            Graph graph = graphScope.getGraphManager().getGraph();

            if(graph.getNodes().size() < 2) return;

            gc.setStroke(Color.RED);
            gc.setFill(Color.BLUE);

            double edgeSize = 4 * graphScope.getEdgeWidth() * graphScope.getNodeSize();

            QuadTree quadTree = new QuadTree(9, graph);
            Stack<Cell> cellStack = new Stack<>();
            cellStack.add(quadTree.getRoot());
            while (!cellStack.isEmpty()){
                Cell cell = cellStack.pop();
                if(cell == null)
                    continue;
                if(cell.getNodes().size() == 0) continue;
                Point2D centerOfMass = cell.getCenterOfMass();
                if(cell.getWidth()/centerOfMass.distance(graph.getNodes().get(0).getPos()) < 1.2) {
                    Point2D center = cell.getCenter();
                    double w = cell.getWidth();
                    gc.setStroke(Color.RED);
                    gc.strokeRect(center.getX() - w/2, center.getY() - w/2, w, w);
                    gc.fillOval(centerOfMass.getX() - edgeSize/2,
                            centerOfMass.getY() - edgeSize/2,
                            edgeSize,
                            edgeSize
                    );
                }else{
                    if(cell.getChildren()[0] != null)
                        cellStack.addAll(Arrays.asList(cell.getChildren()));
                    else{
                        gc.setStroke(Color.GREEN);
                        cell.getNodes().forEach(node -> gc.strokeLine(node.getPos().getX(), node.getPos().getY(),
                                graph.getNodes().get(0).getPos().getX(), graph.getNodes().get(0).getPos().getY()));
                    }
                }
            }

             */
        }

        gc.setTransform(new Affine());

        //select rectangle
        if (inputScope.isSelecting()) {
            drawSelectRect(gc);
        }

        //border
        drawBorder(gc);
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
                .deriveColor(0,1,1,0.4);
        Rectangle2D rect = inputScope.getSelectRect();

        gc.setFill(rectColor);
        gc.fillRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
        gc.setStroke(rectColor.darker().darker());
        gc.setLineWidth(0.3);
        gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight());
    }

    private void drawBorder(GraphicsContext gc){
        int borderWidth = 3;
        double width = canvasScope.getCanvasWidth();
        double height = canvasScope.getCanvasHeight();

        gc.setFill(canvasScope.getBorderColor());
        gc.fillRect(0,0, borderWidth, height);
        gc.fillRect(width-borderWidth, 0, borderWidth, height);
        gc.fillRect(0, 0, width, borderWidth);
        gc.fillRect(0, height-borderWidth, width, borderWidth);
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
