package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.canvas.Painter;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.GraphCoarsener;
import com.todense.viewmodel.layout.LongRangeForce;
import com.todense.viewmodel.layout.barnesHut.Cell;
import com.todense.viewmodel.layout.barnesHut.QuadTree;
import javafx.geometry.Point2D;

import java.util.Arrays;
import java.util.Stack;

public class DynamicLayoutService extends AlgorithmService {

	private GraphManager graphManager;
	private LayoutViewModel layoutVM;

	private int coolingCounter = 0;

	double energy = Double.POSITIVE_INFINITY;

	double[] nodeEnergies;

	private double[][] repulsiveForces;
	private double[][] attractiveForces;
	private double[][] distances;

	private double stepSize;

	private double optDist;
	private double initStep;
	private double tolerance;

	private final double repulsiveness = 0.2d;

	private QuadTree quadTree;

	private double gamma = Math.sqrt(9d/4d);
	private LongRangeForce longRangeForce;
	private Point2D center;

	public DynamicLayoutService(GraphManager graphManager, LayoutViewModel layoutVM, Point2D center){
    	super(graphManager.getGraph());
		this.graphManager = graphManager;
		this.layoutVM = layoutVM;
    	this.center = center;
    	this.optDist  = layoutVM.getOptDist();
    	this.initStep  = layoutVM.getStep();
    	this.stepSize = initStep;
    	this.tolerance = layoutVM.getTolerance();
    	this.longRangeForce = layoutVM.getLongRangeForce();
	}

	@Override
	public void perform() throws InterruptedException {

		if(layoutVM.isMultilevelOn()){
			GraphCoarsener graphCoarsener = new GraphCoarsener(graphManager);
			graphCoarsener.initGraphSequence();

			while(!graphCoarsener.maxLevelReached()){
				graphCoarsener.coarsen();
				painter.sleep();
			}
			optDist = optDist * Math.pow(gamma, graphCoarsener.getGraphSequence().size()-1);

			while(graphCoarsener.getGraphSequence().size() > 1){
				optDist = optDist/gamma;
				stepSize = initStep;
				graphCoarsener.reconstruct(0.3 * optDist);
				painter.sleep();
				forceDirectedLayout(graphCoarsener.getGraphSequence().peek());
			}
		}else{
			forceDirectedLayout(graph);
		}
	}

	@Override
	protected void onFinished() {
	}

	void init(Graph graph){
    	int nodeCount = graph.getNodes().size();
		repulsiveForces = new double[nodeCount][nodeCount];
		attractiveForces = new double[nodeCount][nodeCount];
		nodeEnergies = new double[nodeCount];
		distances = new double[nodeCount][nodeCount];
	}

	public int counter = 0;

	public void forceDirectedLayout(Graph graph) throws InterruptedException {

		init(graph);

		while(!super.isCancelled() &&  stepSize > tolerance * optDist) {
			counter++;

			if(layoutVM.isBarnesHutOn()){
				quadTree = new QuadTree(7, graph);
			}

			//calculate distance matrix
			for (Node n : graph.getNodes()) {
				for (Node m : graph.getNodes()) {
					if(n.getIndex() < m.getIndex()){
						distances[n.getIndex()][m.getIndex()] = n.getPos().distance(m.getPos());
					}
				}
			}

			//calculate repulsive forces
			if(!layoutVM.isBarnesHutOn()) {
				double CK = -repulsiveness * Math.pow(optDist, longRangeForce.getExponent() + 1);
				graph.getNodes().forEach(n -> {
					for (Node m : graph.getNodes()) {
						if (n.getIndex() < m.getIndex()) {
							double dist = getDistance(n, m);
							//if(dist > 0 && dist < 2 * (coarserLevel + 1) * optDist) {
							if (dist > 0) {
								double rf = CK / Math.pow(dist, longRangeForce.getExponent());
								repulsiveForces[n.getIndex()][m.getIndex()] = rf;
								repulsiveForces[m.getIndex()][n.getIndex()] = rf;
							}
						}
					}
				});
			}

			//calculate attractive forces
			graph.getEdges().forEach(e ->{
				double af = Math.pow(getDistance(e.getN1(), e.getN2()), 2) / optDist;
				attractiveForces[e.getN1().getIndex()][e.getN2().getIndex()] = af;
				attractiveForces[e.getN2().getIndex()][e.getN1().getIndex()] = af;
			});

			double prevEnergy = energy;
			energy = 0;

			//apply forces to every node
			graph.getNodes().forEach(n ->{
				Point2D force = new Point2D(0, 0);

				//apply repulsive force to node n
				if(layoutVM.isBarnesHutOn()){
					force = force.add(calcBarnesHutRepulsiveForce(n, quadTree));
				}else{
					for (Node m : graph.getNodes()) {
						double dist = getDistance(n, m);
						//if(dist > 0 && dist < 2 * (coarserLevel + 1) * optDist) {
						if(dist > 0 ) {
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
				if(layoutVM.isPullingOn()) {
					force = force.subtract(n.getPos().subtract(center).multiply(layoutVM.getPullStrength() / optDist));
				}

				nodeEnergies[n.getIndex()] = Math.pow(force.getX(), 2) + Math.pow(force.getY(), 2);

				if(!Double.isNaN(force.getX())){
					n.setPos(n.getPos().add(force.normalize().multiply(stepSize)));
				}
			});

			Arrays.stream(nodeEnergies).forEach(e -> energy += e);

			painter.sleep(1);

			if(layoutVM.isCoolingOn()) {
				stepSize = updateStepLength(energy, prevEnergy);
			}
		}
		super.repaint();
	}

	private Point2D calcBarnesHutRepulsiveForce(Node node, QuadTree quadTree){
		Point2D repulsiveForce = new Point2D(0, 0);
		Stack<Cell> cellStack = new Stack<>();
		cellStack.add(quadTree.getRoot());
		while (!cellStack.isEmpty()){
			Cell cell = cellStack.pop();
			if(cell == null || cell.getNodes().size() == 0) continue;
			Point2D centerOfMass = cell.getCenterOfMass();
			double dist = centerOfMass.distance(node.getPos());
			if(cell.getWidth() / dist < 1.2) {
				double rf = -repulsiveness * cell.getNodes().size() * Math.pow(optDist, longRangeForce.getExponent() + 1) / Math.pow(dist, longRangeForce.getExponent());
				repulsiveForce = repulsiveForce.add((centerOfMass.subtract(node.getPos())).multiply(rf / dist));
			}else{
				if(cell.getChildren()[0] != null)
					cellStack.addAll(Arrays.asList(cell.getChildren()));
				else{
					//if node is too close to be considered by "dist < 1.2" restriction, calculate its repulsive force in the standard way
					for(Node m : cell.getNodes()){
						if(m.equals(node)) continue;
						double rf = -repulsiveness * Math.pow(optDist, longRangeForce.getExponent() + 1) / Math.pow(dist, longRangeForce.getExponent());
						repulsiveForce = repulsiveForce.add((m.getPos().subtract(node.getPos())).multiply(rf / dist));
					}
				}
			}
		}
		return repulsiveForce;
	}

	private double getDistance(Node n, Node m) {
		return n.getIndex() < m.getIndex()
				? distances[n.getIndex()][m.getIndex()]
				: distances[m.getIndex()][n.getIndex()];
	}


	public double updateStepLength(double energy, double energy0) {
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
