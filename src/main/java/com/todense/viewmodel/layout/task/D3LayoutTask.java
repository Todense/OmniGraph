package com.todense.viewmodel.layout.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.LayoutTask;
import com.todense.viewmodel.scope.LayoutScope;
import javafx.geometry.Point2D;

public class D3LayoutTask extends LayoutTask {

    double optDist;
    double alpha;
    double gravity;
    double repulsiveStrength;
    double alphaDecay;

    private long lastIterationStartTime;

    public D3LayoutTask(LayoutScope layoutScope, GraphManager graphManager) {
        super(layoutScope, graphManager);
        super.algorithmName = "D3 Layout";
        layoutScope.setD3Alpha(1.0);
        this.alpha = 1.0;
        this.alphaDecay = layoutScope.getD3AlphaDecay();
    }

    @Override
    protected boolean stopConditionMet() {
        if(iterationCounter < 10){
            return false;
        }
        return super.isCancelled() || Math.abs(alpha - layoutScope.getD3MinAlpha()) < layoutScope.getD3Tolerance();
    }

    @Override
    protected void applyRepulsiveForce(Node n, Node m, boolean opposite) {
        Point2D mPos = m.getPos();
        Point2D nPos = n.getPos();
        if(mPos.equals(nPos)){
            mPos = addNoise(mPos);
        }
        Point2D delta = mPos.subtract(nPos);
        double dist = delta.magnitude();
        double rf = -repulsiveStrength * alpha / Math.pow(dist, 2);
        Point2D force = delta.multiply(rf);
        addForce(n, force);

        if(opposite){
            addForce(m, force.multiply(-1));
        }
    }

    @Override
    protected void applyRepulsiveForce(Node node, Point2D centerOfMass, double centerDist, int cellSize) {
        double rf = cellSize * -repulsiveStrength * alpha / Math.pow(centerDist,2);
        Point2D force = (centerOfMass.subtract(node.getPos())).multiply(rf);
        addForce(node, force);
    }

    @Override
    protected void applyAttractiveForce(Node n, Node m) {
        Point2D mPos = m.getPos().add(getForce(m));
        Point2D nPos = n.getPos().add(getForce(n));
        if(mPos.equals(nPos)){
            mPos = addNoise(mPos);
        }
        double strength = 1.0 / (Math.min(n.getDegree(), m.getDegree()) + 1);
        Point2D delta = mPos.subtract(nPos);
        double dist = delta.magnitude();
        double af = alpha * strength * (dist - optDist) / dist;
        Point2D force = delta.multiply(af);
        //double bias = (double)n.getDegree()/(m.getDegree()+n.getDegree());
        addForce(n, force);
        addForce(m, force.multiply(-1));
    }

    @Override
    protected void applyGravity(Node n) {
        addForce(n, n.getPos().multiply(-gravity));
        //addForce(n, new Point2D(0, -1).multiply(-gravity));
    }

    @Override
    protected void onIterationStart(Graph graph) {
        lastIterationStartTime = System.currentTimeMillis();

        double alphaDelta = (layoutScope.getD3OptimalAlpha() - layoutScope.getD3Alpha()) *
                alphaDecay;
        layoutScope.setD3Alpha(layoutScope.getD3Alpha() + alphaDelta);

        alpha = layoutScope.getD3Alpha();

        if(!layoutScope.isMultilevelOn()){
            alphaDecay = layoutScope.getD3AlphaDecay();
            optDist = layoutScope.getD3OptDist();
            repulsiveStrength = layoutScope.getD3RepulsiveStrength();
        }

        gravity = alpha * layoutScope.gravityPullStrength() / optDist;

        boolean dragging = graph.getNodes().stream().anyMatch(Node::isDragged);
        if(dragging){
            layoutScope.setD3OptimalAlpha(0.7);
        }else{
            if(graph.isTopologyChanged()){
                layoutScope.setD3Alpha(0.5);
                graph.setTopologyChanged(false);
            }
            layoutScope.setD3OptimalAlpha(layoutScope.getD3MinAlpha());
        }
    }

    @Override
    protected void onIterationEnd() {
        long iterTime = System.currentTimeMillis()-lastIterationStartTime;
        if(iterTime < 7){
            try {
                super.sleep((int) (7-iterTime));
            } catch (InterruptedException ignored) {
            }
        }
        lastIterationStartTime = System.currentTimeMillis();
    }

    @Override
    protected void updateGraph(Graph graph) {
        for (Node node: graph.getNodes()) {
            multiplyForce(node, 1-layoutScope.getD3SpeedDecay());

            if(node.isDragged() || node.isPinned()){
                setForce(node, new Point2D(0,0));
            }

            Point2D force = getForce(node);
            double magnitude = force.magnitude();
            if (magnitude > 1.0e3) {
                Point2D cappedForce = force.multiply(1.0e3 / magnitude);
                setForce(node, cappedForce);
                force = cappedForce;
            }
            Point2D updatedPos = node.getPos().add(force);
            graph.setNodePosition(node, updatedPos, false);
        }
    }

    @Override
    protected void updateMultiLayoutParameters() {
        layoutScope.setD3Alpha(1.0);
        alpha = 1.0;
        alphaDecay = alphaDecay / gamma;
        optDist = optDist / gamma;
        repulsiveStrength = repulsiveStrength / gamma;
    }

    @Override
    protected void initMultilevelParameters() {
        double multiplier = Math.pow(gamma, super.getGraphSequenceLength()-1);
        alphaDecay = layoutScope.getD3AlphaDecay() * multiplier;
        optDist = layoutScope.getD3OptDist() * multiplier;
        repulsiveStrength = layoutScope.getD3RepulsiveStrength() * multiplier;
    }
}
