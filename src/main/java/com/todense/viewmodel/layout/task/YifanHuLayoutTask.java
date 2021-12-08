package com.todense.viewmodel.layout.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.LayoutTask;
import com.todense.viewmodel.scope.LayoutScope;
import javafx.geometry.Point2D;

import java.util.Arrays;

public class YifanHuLayoutTask extends LayoutTask {

	private double repulsiveStrength;
	private double optDist;
	private double longRangeExponent;
	private double gravity;
	private double previousGraphEnergy;
	private double graphEnergy;
	private int coolingCounter;

	private double[][] distances;


	public YifanHuLayoutTask(LayoutScope layoutScope, GraphManager graphManager) {
		super(layoutScope, graphManager);
		super.algorithmName = "Yifan Hu Layout";
		layoutScope.setStepSize(layoutScope.getInitialStepSize());
	}

	@Override
	protected boolean stopConditionMet() {
		return super.isCancelled() || layoutScope.getStepSize() < layoutScope.getTolerance() * optDist;
	}

	@Override
	protected void applyRepulsiveForce(Node n, Node m, boolean opposite) {
		double dist = getDistance(n, m);
		if (dist > 0) {
			double rf = repulsiveStrength / Math.pow(dist, layoutScope.getLongRangeForce());
			Point2D diff = m.getPos().subtract(n.getPos()).multiply(rf/dist);
			addForce(n, diff);
			if(opposite){
				addForce(m, diff.multiply(-1));
			}
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
		double dist = getDistance(n, m);
		double af = Math.pow(dist, 2) / optDist;
		Point2D diff = (m.getPos().subtract(n.getPos())).multiply(af/dist);
		addForce(n, diff);
		addForce(m, diff.multiply(-1));
	}

	@Override
	protected void applyGravity(Node n) {
		addForce(n, n.getPos().multiply(-gravity));
	}

	@Override
	protected void onIterationStart(Graph graph) {
		optDist = layoutScope.getOptDist();
		repulsiveStrength = getRepulsiveStrength(1.0);
		longRangeExponent = layoutScope.getLongRangeForce();
		gravity = layoutScope.gravityPullStrength() / optDist;

		calculateDistanceMatrix(graph);
	}

	@Override
	protected void onIterationEnd() {
		updateStepSize();
		try {
			super.sleep(1);
		} catch (InterruptedException ignored) {
		}
	}

	@Override
	protected void initForces() {
		forces = new Point2D[graph.getOrder()];
		Arrays.fill(forces, new Point2D(0,0));
	}

	@Override
	protected void updateMultiLayoutParameters() {
		double stepSize = layoutScope.getInitialStepSize()/gamma;
		layoutScope.setStepSize(stepSize);
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
				graph.setNodePosition(node, updatedPos, false);
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
		return -repulsiveness * Math.pow(layoutScope.getOptDist(), longRangeExponent);
	}

	private void calculateDistanceMatrix(Graph graph){
		distances = new double[graph.getOrder()][graph.getOrder()];
		for (Node n : graph.getNodes()) {
			for (Node m : graph.getNodes()) {
				if(n.getIndex() < m.getIndex()){
					distances[n.getIndex()][m.getIndex()] = n.getPos().distance(m.getPos());
				}
			}
		}
	}

	protected double getDistance(Node n, Node m) {
		return n.getIndex() < m.getIndex()
				? distances[n.getIndex()][m.getIndex()]
				: distances[m.getIndex()][n.getIndex()];
	}
}
