package com.todense.viewmodel.canvas.drawlayer.layers;

import com.todense.model.graph.Graph;
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

    int level = 8;

    public UpperDrawLayer(GraphScope graphScope, InputScope inputScope, CanvasScope canvasScope, BackgroundScope backgroundScope, AlgorithmScope algorithmScope){
        this.graphScope = graphScope;
        this.inputScope = inputScope;
        this.canvasScope = canvasScope;
        this.backgroundScope = backgroundScope;
        this.algorithmScope = algorithmScope;
    }

    public Rectangle2D getGraphBoundary(Graph graph){

        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;
        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Node node : graph.getNodes()) {
            double x = node.getPos().getX();
            if(x < xMin) xMin = x;
            if(x > xMax) xMax = x;

            double y = node.getPos().getY();
            if(y < yMin) yMin = y;
            if(y > yMax) yMax = y;
        }
        double squareLength = Math.max(xMax-xMin, yMax-yMin);
        return new Rectangle2D(xMin, yMin, squareLength, squareLength);
    }

    @Override
    public void draw(GraphicsContext gc) {

        //endpoints
        if(graphScope.getDisplayMode() != DisplayMode.ANT_COLONY && algorithmScope.isShowingEndpoints()) {

            double size = graphScope.getNodeSize() * 0.5;
            double triangleSize = size * 0.9;
            double lineWidth = graphScope.getNodeSize() * 0.15;

            Node startNode = algorithmScope.getStartNode();
            Node goalNode = algorithmScope.getGoalNode();

            if (algorithmScope.getAlgorithm().isWithGoal() && goalNode != null) {
                drawMarker(gc, goalNode, lineWidth);

                Point2D goalPt = goalNode.getPos().add(new Point2D(0, 1).multiply(size + lineWidth * 0.9));
                double[] x = new double[]{goalPt.getX(), goalPt.getX() - triangleSize, goalPt.getX() + triangleSize};
                double[] y = new double[]{goalPt.getY(), goalPt.getY() + triangleSize, goalPt.getY() + triangleSize};

                gc.fillPolygon(x, y, 3);
            }
            if (algorithmScope.getAlgorithm().isWithStart() && startNode != null) {
                drawMarker(gc, startNode, lineWidth);

                Point2D startPt = startNode.getPos().add(new Point2D(0, -1).multiply(size + lineWidth * 0.9));
                double[] x = new double[]{startPt.getX(), startPt.getX() - triangleSize, startPt.getX() + triangleSize};
                double[] y = new double[]{startPt.getY(), startPt.getY() - triangleSize, startPt.getY() - triangleSize};

                gc.fillPolygon(x, y, 3);
            }

            Graph graph = graphScope.getGraphManager().getGraph();
            if(graph.getNodes().size() == 0) return;
            /*
            Rectangle2D rect0 = getGraphBoundary(graphScope.getGraphManager().getGraph());
            gc.setStroke(Color.RED);
            gc.strokeRect(rect0.getMinX(), rect0.getMinY(), rect0.getWidth(), rect0.getHeight());
            List<Rectangle2D> rectangles = new ArrayList<>();
            rectangles.add(rect0);
            for (int i = 0; i < level; i++) {
                List<Rectangle2D> newRectangles = new ArrayList<>();
                for(Rectangle2D rect : rectangles){
                    double width = rect.getWidth()/2;
                    double height = rect.getHeight()/2;
                    Rectangle2D newRect = new Rectangle2D(rect.getMinX(), rect.getMinY(), width, height);
                    if(containsNode(newRect, graph)) newRectangles.add(newRect);
                    newRect = new Rectangle2D(rect.getMinX() + width, rect.getMinY(), width, height);
                    if(containsNode(newRect, graph)) newRectangles.add(newRect);
                    newRect = new Rectangle2D(rect.getMinX(), rect.getMinY() + height, width, height);
                    if(containsNode(newRect, graph)) newRectangles.add(newRect);
                    newRect = new Rectangle2D(rect.getMinX() + width, rect.getMinY() + height, width, height);
                    if(containsNode(newRect, graph)) newRectangles.add(newRect);
                }
                newRectangles.forEach(rect -> gc.strokeRect(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight()));
                rectangles = newRectangles;
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

    private boolean containsNode(Rectangle2D rect, Graph graph){
        boolean good = false;
        var nodes = graph.getNodes();
        for(Node n : nodes){
            if(rect.contains(n.getPos())){
                good = true;
                break;
            }
        }
        return good;
    }

    private void drawMarker(GraphicsContext gc, Node node, double lineWidth) {
        Point2D pos = node.getPos();
        gc.setStroke(backgroundScope.getBackgroundColor().invert().grayscale());
        gc.setFill(backgroundScope.getBackgroundColor().invert().grayscale());
        double size = graphScope.getNodeSize() + lineWidth;
        gc.setLineWidth(lineWidth);
        gc.strokeOval(pos.getX() - size/2, pos.getY() - size/2, size, size);
    }

    private void drawSelectRect(GraphicsContext gc){
        Color rectColor = backgroundScope.getBackgroundColor().invert().grayscale().deriveColor(0,1,1,0.4);
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

        gc.setFill(backgroundScope.getBackgroundColor().invert().darker().grayscale());
        gc.fillRect(0,0, borderWidth, height);
        gc.fillRect(width-borderWidth, 0,borderWidth, height);
        gc.fillRect(0, 0, width, borderWidth);
        gc.fillRect(0, height-borderWidth, width, borderWidth);
    }

    @Override
    public int getOrder() {
        return 4;
    }
}
