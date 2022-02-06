package com.todense.viewmodel.layout.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.LayoutTask;
import com.todense.viewmodel.scope.LayoutScope;
import javafx.geometry.Point2D;

import java.util.Arrays;

public class AdaptiveCoolingLayoutTask extends LayoutTask {

	private double repulsiveStrength;
	private double optDist;
	private double longRangeExponent;
	private double gravity;
	private double previousGraphEnergy;
	private double graphEnergy;
	private int coolingCounter;


	public AdaptiveCoolingLayoutTask(LayoutScope layoutScope, GraphManager graphManager) {
		super(layoutScope, graphManager);
		super.algorithmName = "Yifan Hu Layout";
		layoutScope.setStepSize(layoutScope.getInitialStepSize());
	}

	@Override
	protected boolean stopConditionMet() {
		if(iterationCounter < 10){
			return false;
		}
		return super.isCancelled() || layoutScope.getStepSize() < layoutScope.getTolerance() * optDist;
	}

	@Override
	protected void applyRepulsiveForce(Node n, Node m, boolean opposite) {
		Point2D nPos = n.getPos();
		Point2D mPos = m.getPos();
		if(nPos.equals(mPos)){
			mPos = addNoise(mPos);
		}
		Point2D delta = mPos.subtract(nPos);
		double dist = delta.magnitude();
		double rf = repulsiveStrength / Math.pow(dist, layoutScope.getLongRangeForce());
		Point2D force = delta.multiply(rf/dist);
		addForce(n, force);
		if(opposite){
			addForce(m, force.multiply(-1));
		}
	}

	@Override
	protected void applyRepulsiveForce(Node node, Point2D centerOfMass, double centerDist, int cellSize) {
		double rf = cellSize * repulsiveStrength / Math.pow(centerDist, longRangeExponent);
		Point2D force = (centerOfMass.subtract(node.getPos())).multiply(rf / centerDist);
		addForce(node, force);
	}

	@Override
	protected void applyAttractiveForce(Node n, Node m) {
		Point2D nPos = n.getPos();
		Point2D mPos = m.getPos();
		if(nPos.equals(mPos)){
			mPos = addNoise(mPos);
		}
		Point2D delta = mPos.subtract(nPos);
		double dist = delta.magnitude();
		double af = Math.pow(dist, 2) / optDist;
		Point2D force = delta.multiply(af/dist);
		addForce(n, force);
		addForce(m, force.multiply(-1));
	}

	@Override
	protected void applyGravity(Node n) {
		addForce(n, n.getPos().multiply(-gravity));
	}

	@Override
	protected void onIterationStart(Graph graph) {
		if(!layoutScope.isMultilevelOn()){
			optDist = layoutScope.getHuOptDist();
		}
		repulsiveStrength = getRepulsiveStrength(1.0);
		longRangeExponent = layoutScope.getLongRangeForce();
		gravity = layoutScope.gravityPullStrength() / optDist;
	}

	@Override
	protected void onIterationEnd() {
		updateStepSize();
	}

	@Override
	protected void initForces() {
		forces = new Point2D[graph.getOrder()];
		Arrays.fill(forces, new Point2D(0,0));
	}

	@Override
	protected void updateMultiLayoutParameters() {
		optDist = optDist / GAMMA;
		layoutScope.setStepSize(layoutScope.getInitialStepSize());
	}

	@Override
	protected void initMultilevelParameters() {
		optDist = layoutScope.getHuOptDist() * Math.pow(GAMMA, super.getGraphSequenceLength()-1);
	}

	@Override
	protected void updateGraph(Graph graph) {
		previousGraphEnergy = graphEnergy;
		graphEnergy = 0;

		double stepSize = layoutScope.getStepSize();

		for (Node node: graph.getNodes()) {
			Point2D force = getForce(node);
			graphEnergy += force.magnitude();
			Point2D forceStepSize = force.normalize().multiply(stepSize);
			Point2D updatedPos = node.getPos().add(forceStepSize);
			if (!node.isDragged() && !node.isPinned()) {
				graph.setNodePosition(node, updatedPos);
			}
		}
	}

	private void updateStepSize(){
		if(layoutScope.isCoolingOn()) {
			coolDownStepSize(graphEnergy, previousGraphEnergy);
		}
	}

	public void coolDownStepSize(double energy, double energy0) {

		double oldStepSize = layoutScope.getStepSize();

		if(energy < energy0) {
			coolingCounter++;
			if(coolingCounter >= 5) { //if energy was reduced 5 times in a row, decrease step magnitude
				coolingCounter = 0;
				double newStepSize = oldStepSize / (1 - layoutScope.getCoolingSpeed());
				layoutScope.setStepSize(newStepSize);
			}
		}
		else {
			coolingCounter = 0;
			double newStepSize = (1 - layoutScope.getCoolingSpeed()) * oldStepSize;
			layoutScope.setStepSize(newStepSize);
		}
	}

	private double getRepulsiveStrength(double repulsiveness){
		return -repulsiveness * Math.pow(layoutScope.getHuOptDist(), longRangeExponent);
	}
}
