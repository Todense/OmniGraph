package com.todense.viewmodel.algorithm.task;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.canvas.Painter;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.GraphCoarsener;
import com.todense.viewmodel.layout.LongRangeForce;
import com.todense.viewmodel.layout.barnesHut.Cell;
import com.todense.viewmodel.layout.barnesHut.QuadTree;
import javafx.geometry.Point2D;

import java.util.*;

public class ForceDirectedLayoutTask extends AlgorithmTask {

	private final GraphManager graphManager;
	private final LayoutViewModel layoutVM;

	private int coolingCounter = 0;
	private final boolean smoothModeOn;

	double energy = Double.POSITIVE_INFINITY;

	double[] nodeEnergies;

	private double[][] repulsiveForces;
	private double[][] attractiveForces;
	private double[][] distances;

	private double stepSize;

	private double optDist;
	private final double initStep;
	private final double tolerance;
	private final double repulsiveStrength;

	private final double repulsiveness = 1d;
	private int iterationCounter = 0;

	private QuadTree quadTree;

	private final double gamma = Math.sqrt(9d/4d);
	protected LongRangeForce longRangeForce;
	private final Point2D center;


	public ForceDirectedLayoutTask(GraphManager graphManager, LayoutViewModel layoutVM, Point2D center){
    	super(graphManager.getGraph());
		this.graphManager = graphManager;
		this.layoutVM = layoutVM;
    	this.center = center;
    	this.optDist  = layoutVM.getOptDist();
    	this.initStep  = layoutVM.getStep();
    	this.stepSize = initStep;
    	this.tolerance = layoutVM.getTolerance();
    	this.longRangeForce = layoutVM.getLongRangeForce();
    	this.repulsiveStrength = getRepulsiveStrength(repulsiveness, optDist, longRangeForce.getExponent() + 1);
    	this.smoothModeOn = layoutVM.isSmoothnessOn();
    	super.algorithmName = "Force Directed Layout";
	}

	@Override
	public void perform() {
		if(layoutVM.isMultilevelOn()){
			multilevelForceDirectedLayout();
		}else{
			try {
				forceDirectedLayout(graph);
			} catch (InterruptedException ignored) {
			}
		}
	}

	@Override
	protected void onFinished() {
	}


	void init(Graph graph){
    	int nodeCount = graph.getOrder();
		repulsiveForces = new double[nodeCount][nodeCount];
		attractiveForces = new double[nodeCount][nodeCount];
		nodeEnergies = new double[nodeCount];
		distances = new double[nodeCount][nodeCount];

		var smoothedNodePositionMap = layoutVM.getNodeSmoothedPositionMap();
		for (Node n: graph.getNodes()){
			smoothedNodePositionMap.put(n, n.getPos());
		}
	}

	// main algorithm
	public void forceDirectedLayout(Graph graph) throws InterruptedException {
		init(graph);
		while(!super.isCancelled() &&  stepSize > tolerance * optDist) {
			iterationCounter++;

			if(layoutVM.isBarnesHutOn()){
				quadTree = new QuadTree(7, graph);
			}

			calculateDistanceMatrix(graph);

			if(!layoutVM.isBarnesHutOn()) {
				calculateRepulsiveForces(graph);
			}

			calculateAttractiveForces(graph);
			List<Point2D> forceList = createForceList(graph);
			updateNodePositions(graph, forceList);
			stepSize = updateStepSize();
			super.sleep(1);
		}

		//transition between smoothed and real position
		if(smoothModeOn){
			finalSmoothTransition(30, 0.8, 20);
		}
		painter.repaint();
	}


	public void multilevelForceDirectedLayout(){
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
		optDist = optDist * Math.pow(gamma, graphCoarsener.getGraphSequence().size()-1);
		while(graphCoarsener.getGraphSequence().size() > 1){
			optDist = optDist/gamma;
			stepSize = initStep;
			graphCoarsener.reconstruct(0.3 * optDist);
			try {
				painter.sleep();
				forceDirectedLayout(graphCoarsener.getGraphSequence().peek());
			} catch (InterruptedException ignored) {
				if(graphManager.getGraph().getOrder() < graphCoarsener.getOriginalGraph().getOrder()){
					graphManager.setGraph(graphCoarsener.getOriginalGraph());
				}
			}
		}
	}

	private void calculateDistanceMatrix(Graph graph){
		for (Node n : graph.getNodes()) {
			for (Node m : graph.getNodes()) {
				if(n.getIndex() < m.getIndex()){
					distances[n.getIndex()][m.getIndex()] = n.getPos().distance(m.getPos());
				}
			}
		}
	}

	private void calculateRepulsiveForces(Graph graph){
		graph.getNodes().forEach(n -> {
			for (Node m : graph.getNodes()) {
				if (n.getIndex() < m.getIndex()) {
					double dist = getDistance(n, m);
					if (dist > 0) {
						double rf = repulsiveStrength / Math.pow(dist, longRangeForce.getExponent());
						repulsiveForces[n.getIndex()][m.getIndex()] = rf;
						repulsiveForces[m.getIndex()][n.getIndex()] = rf;
					}
				}
			}
		});
	}

	private void calculateAttractiveForces(Graph graph){
		graph.getEdges().forEach(e ->{
			double af = Math.pow(getDistance(e.getN1(), e.getN2()), 2) / optDist;
			attractiveForces[e.getN1().getIndex()][e.getN2().getIndex()] = af;
			attractiveForces[e.getN2().getIndex()][e.getN1().getIndex()] = af;
		});
	}


	private List<Point2D> createForceList(Graph graph){

		LinkedList<Point2D> nodeForceMap = new LinkedList<>();

		for(Node n: graph.getNodes()){
			Point2D force = new Point2D(0, 0);

			//apply repulsive force to node n
			if(layoutVM.isBarnesHutOn()){
				force = force.add(calcBarnesHutRepulsiveForce(n, repulsiveStrength, quadTree));
			}
			else{
				for (Node m : graph.getNodes()) {
					double dist = getDistance(n, m);
					if(dist > 0) {
						double rf = repulsiveForces[n.getIndex()][m.getIndex()];
						force = force.add(m.getPos().subtract(n.getPos()).multiply(rf/dist));
					}
				}
			}


			//apply attractive force to node n
			for (Node m : n.getNeighbours()) {
				double dist = getDistance(n, m);
				if(dist > 0){
					double af = attractiveForces[n.getIndex()][m.getIndex()];
					force = force.add((m.getPos().subtract(n.getPos())).multiply(af/dist));
				}
			}

			//apply center pull
			if(layoutVM.isCenterPull()) {
				force = force.subtract(n.getPos().subtract(center).multiply(layoutVM.getCenterPullStrength() / optDist));
			}

			nodeEnergies[n.getIndex()] = Math.pow(force.getX(), 2) + Math.pow(force.getY(), 2);
			nodeForceMap.add(force.normalize().multiply(stepSize));
		}
		return nodeForceMap;
	}

	private void updateNodePositions(Graph graph, List<Point2D> forceList){
		int i = 0;
		double smoothness = layoutVM.getSmoothness();
		for(Point2D force: forceList){
			Node node = graph.getNodes().get(i++);
			Point2D updatedPos = node.getPos().add(force);
			if(layoutVM.isSmoothnessOn()){
				Point2D smoothedPos = updatedPos.multiply(1-smoothness)
						.add(layoutVM.getNodeSmoothedPositionMap().get(node).multiply(smoothness));
				layoutVM.getNodeSmoothedPositionMap().replace(node, smoothedPos);
			}
			else{
				layoutVM.getNodeSmoothedPositionMap().replace(node, updatedPos);
			}
			node.setPos(updatedPos);
		}
	}

	private double updateStepSize(){
		double newStepSize;
		double prevEnergy = energy;
		energy = Arrays.stream(nodeEnergies).sum();

		if(layoutVM.isCoolingOn()) {
			newStepSize = coolDownStepSize(energy, prevEnergy);
		}else{
			newStepSize = layoutVM.getStep();
		}
		return newStepSize;
	}
	
	private void finalSmoothTransition(int nSteps, double stepMulti, int stepTime) throws InterruptedException {
		for(int i = 0; i < nSteps; i++){
			for(Node node: graph.getNodes()){
				var currentPos = layoutVM.getNodeSmoothedPositionMap().get(node);
				layoutVM.getNodeSmoothedPositionMap()
						.replace(node, node.getPos().add(currentPos.subtract(node.getPos()).multiply(stepMulti)));
			}
			super.sleep(stepTime);
		}
	}

	private Point2D calcBarnesHutRepulsiveForce(Node node, double strength, QuadTree quadTree){
		Point2D repulsiveForce = new Point2D(0, 0);
		Stack<Cell> cellStack = new Stack<>();
		cellStack.add(quadTree.getRoot());
		while (!cellStack.isEmpty()){
			Cell cell = cellStack.pop();
			if(cell == null || cell.getNodes().size() == 0) continue;
			Point2D centerOfMass = cell.getCenterOfMass();
			double centerDist = centerOfMass.distance(node.getPos());
			if(cell.getWidth() / centerDist < 1.2) {
				double rf = cell.getNodes().size() * strength / Math.pow(centerDist, longRangeForce.getExponent());
				repulsiveForce = repulsiveForce.add((centerOfMass.subtract(node.getPos())).multiply(rf / centerDist));
			}else{
				if(cell.getChildren()[0] != null)
					cellStack.addAll(Arrays.asList(cell.getChildren()));
				else{
					//if a node is too close to be considered by "cell.getWidth() / dist < 1.2" restriction,
					// calculate its repulsive force in the standard way
					for(Node m : cell.getNodes()){
						if(m.equals(node))
							continue;
						double dist = getDistance(node, m);
						double rf = strength / Math.pow(dist, longRangeForce.getExponent());
						repulsiveForce = repulsiveForce.add((m.getPos().subtract(node.getPos())).multiply(rf / dist));
					}
				}
			}
		}
		return repulsiveForce;
	}

	private double getRepulsiveStrength(double repulsiveness, double optDist, double exponent){
		return -repulsiveness * Math.pow(optDist, exponent);
	}

	protected double getDistance(Node n, Node m) {
		return n.getIndex() < m.getIndex()
				? distances[n.getIndex()][m.getIndex()]
				: distances[m.getIndex()][n.getIndex()];
	}


	public double coolDownStepSize(double energy, double energy0) {
		if(energy < energy0) {
			coolingCounter++;
			if(coolingCounter >= 5) { //if energy was reduced 5 times in a row, decrease step magnitude
				coolingCounter = 0;
				stepSize = stepSize / (1 - layoutVM.getCoolingStrength());
			}
		}
		else {
			coolingCounter = 0;
			stepSize = (1 - layoutVM.getCoolingStrength()) * stepSize;
		}
		return stepSize;
	}

	@Override
	public void setPainter(Painter painter) {
		super.setPainter(painter);
	}
}
