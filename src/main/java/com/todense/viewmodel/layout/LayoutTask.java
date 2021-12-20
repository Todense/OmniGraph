package com.todense.viewmodel.layout;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.barnesHut.Cell;
import com.todense.viewmodel.layout.barnesHut.QuadTree;
import com.todense.viewmodel.scope.LayoutScope;
import javafx.geometry.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public abstract class LayoutTask extends AlgorithmTask {

    protected int iterationCounter = 0;
    protected LayoutScope layoutScope;
    protected GraphManager graphManager;
    private QuadTree quadTree;
    protected Point2D [] forces;
    protected Point2D [] prevForces;
    protected HashMap<Node, Integer> prevNodeIdx;
    protected final double gamma = Math.sqrt(9d/4d);
    Random rnd = new Random();

    private int graphSequenceLength = 0;

    public LayoutTask(LayoutScope layoutScope, GraphManager graphManager) {
        super(graphManager.getGraph());
        this.layoutScope = layoutScope;
        this.graphManager = graphManager;
        this.prevNodeIdx = new HashMap<>();
    }

    @Override
    public void perform() throws InterruptedException {
        if(layoutScope.isMultilevelOn()){
            multilevelLayout(graphManager);
        }else{
            layout(graph);
        }
    }

    @Override
    protected void onFinished() {

    }

    protected void layout(Graph graph) throws InterruptedException{
        iterationCounter = 0;
        while(!stopConditionMet()){
            try {
                waitIfNoNodes();
            } catch (InterruptedException e){
                break;
            }

            iterationCounter++;
            synchronized (Graph.LOCK) {
                initForces();

                if(layoutScope.isBarnesHutOn()){
                    quadTree = new QuadTree(7, graph);
                }

                onIterationStart(graph);
                applyForces(graph);
                updateGraph(graph);
            }
            onIterationEnd();
        }
        super.repaint();
    }

    void multilevelLayout(GraphManager graphManager){
        double optDist = layoutScope.getHuOptDist();
        GraphCoarsener graphCoarsener = new GraphCoarsener(graphManager);
        graphCoarsener.initGraphSequence();
        while(!graphCoarsener.maxLevelReached()){
            graphCoarsener.coarsen();
            try {
                super.sleep();
            } catch (InterruptedException ignored) {
                if(graphManager.getGraph().getOrder() < graphCoarsener.getOriginalGraph().getOrder()){
                    graphManager.setGraph(graphCoarsener.getOriginalGraph());
                }
            }
        }
        graphSequenceLength = graphCoarsener.getGraphSequence().size();
        initMultilevelParameters();
        optDist = optDist * Math.pow(gamma, graphCoarsener.getGraphSequence().size()-1);
        while(graphCoarsener.getGraphSequence().size() > 1){
            optDist = optDist/gamma;
            updateMultiLayoutParameters();
            graphCoarsener.reconstruct();
            try {
                super.sleep();
                layout(graphCoarsener.getGraphSequence().peek());
            } catch (InterruptedException ignored) {
                if(graphManager.getGraph().getOrder() < graphCoarsener.getOriginalGraph().getOrder()){
                    graphManager.setGraph(graphCoarsener.getOriginalGraph());
                }
            }
        }
    }


    void applyForces(Graph graph){
        if(layoutScope.isBarnesHutOn()){
            for (int i = 0; i < graph.getOrder(); i++) {
                Node n = graph.getNodes().get(i);
                applyBarnesHutRepulsiveForces(n, quadTree);
            }
        }else{
            for (int i = 0; i < graph.getOrder(); i++) {
                Node n = graph.getNodes().get(i);
                for (int j = i+1; j < graph.getOrder(); j++) {
                    Node m = graph.getNodes().get(j);
                    applyRepulsiveForce(n, m, true);
                }
            }
        }
        for(Edge e: graph.getEdges()){
            Node n = e.getN1();
            Node m = e.getN2();
            applyAttractiveForce(n, m);
        }

        if(layoutScope.isGravityOn()){
            for(Node n: graph.getNodes()){
                applyGravity(n);
            }
        }
    }

    protected abstract boolean stopConditionMet();

    private void applyBarnesHutRepulsiveForces(Node node, QuadTree quadTree){
        Stack<Cell> cellStack = new Stack<>();
        cellStack.add(quadTree.getRoot());
        while (!cellStack.isEmpty()){
            Cell cell = cellStack.pop();
            if(cell == null || cell.getNodes().size() == 0) continue;
            Point2D centerOfMass = cell.getCenterOfMass();
            if(centerOfMass.equals(node.getPos())){
                centerOfMass = addNoise(centerOfMass);
            }
            double centerDist = centerOfMass.distance(node.getPos());
            if(cell.getWidth() / centerDist < 1.2) {
                applyRepulsiveForce(node, centerOfMass, centerDist, cell.getNodes().size());
            }else{
                if(cell.getChildren()[0] != null)
                    cellStack.addAll(Arrays.asList(cell.getChildren()));
                else{
                    //if a node is too close to be considered by "cell.getWidth() / dist < 1.2" restriction,
                    // calculate its repulsive force in the standard way
                    for(Node m : cell.getNodes()){
                        if(m.equals(node))
                            continue;
                        applyRepulsiveForce(node, m, false);
                    }
                }
            }
        }
    }

    private void waitIfNoNodes() throws InterruptedException {
        while (graph.getOrder() == 0){
            super.sleep(100);
        }
    }

    protected void initForces(){
        prevForces = forces;
        forces = new Point2D[graph.getOrder()];
        for(Node n: graph.getNodes()){
            var prevIndex = prevNodeIdx.getOrDefault(n, -1);
            Point2D prevForce = prevIndex == -1 ?
                    new Point2D(0,0) :
                    prevForces[prevIndex];
            forces[n.getIndex()] = prevForce;
            prevNodeIdx.put(n, n.getIndex());
        }
    }

    protected Point2D addNoise(Point2D p){
        return p.add(rnd.nextDouble()*1.0e-4-0.5e-4, rnd.nextDouble()*1.0e-4-0.5e-4);
    }

    protected void addForce(Node n, Point2D force){
        forces[n.getIndex()] = forces[n.getIndex()].add(force);
    }

    protected Point2D getForce(Node n){
        return forces[n.getIndex()];
    }

    protected void multiplyForce(Node n, double d){
        forces[n.getIndex()] = forces[n.getIndex()].multiply(d);
    }

    protected void setForce(Node n, Point2D f){
        forces[n.getIndex()] = f;
    }

    protected abstract void applyRepulsiveForce(Node n, Node m, boolean opposite);

    protected abstract void applyRepulsiveForce(Node node, Point2D centerOfMass, double centerDist, int cellSize);

    protected abstract void applyAttractiveForce(Node n, Node m);

    protected abstract void applyGravity(Node n);

    protected abstract void onIterationStart(Graph graph);

    protected abstract void onIterationEnd();

    protected abstract void updateGraph(Graph graph);

    protected abstract void initMultilevelParameters();

    protected abstract void updateMultiLayoutParameters();

    public int getIterationCounter() {
        return iterationCounter;
    }

    public int getGraphSequenceLength() {
        return graphSequenceLength;
    }
}
