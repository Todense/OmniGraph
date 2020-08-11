package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.canvas.Painter;
import com.todense.viewmodel.graph.GraphManager;
import com.todense.viewmodel.layout.GraphCoarser;
import javafx.geometry.Point2D;

import java.util.Arrays;

public class DynamicLayoutService extends AlgorithmService {

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
	private int coarserLevel;

	double gamma = Math.sqrt(7d/4d);

	private Point2D center;

	private GraphCoarser graphCoarser;

	public DynamicLayoutService(GraphManager graphManager, LayoutViewModel layoutVM, Point2D center){
    	super(graphManager.getGraph());
		this.layoutVM = layoutVM;
    	this.center = center;
    	this.optDist  = layoutVM.getOptDist();
    	this.initStep  = layoutVM.getStep();
    	this.stepSize = initStep;
    	this.tolerance = layoutVM.getTolerance();
    	this.graphCoarser = new GraphCoarser(graphManager);
	}

	@Override
	public void perform() throws InterruptedException {
		if(layoutVM.isMultilevelOn()){
			graphCoarser.createList();
			coarserLevel = graphCoarser.getGraphList().size();
			while(graphCoarser.getGraphList().size() > 1){
				optDist = optDist/gamma;
				//initStep = initStep/gamma;
				stepSize = initStep;
				tolerance = tolerance/(gamma);
				graphCoarser.coarserDown();
				forceDirectedLayout(graphCoarser.getGraphList().peek());
				coarserLevel--;
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
    	//stepSize = initStep;
		repulsiveForces = new double[nodeCount][nodeCount];
		attractiveForces = new double[nodeCount][nodeCount];
		nodeEnergies = new double[nodeCount];
		distances = new double[nodeCount][nodeCount];
	}

	public int counter = 0;

	public void forceDirectedLayout(Graph graph) throws InterruptedException {

		init(graph);

		while(!super.isCancelled() &&  stepSize > layoutVM.getTolerance()) {
			counter++;

			//calculate distance matrix
			for (Node n : graph.getNodes()) {
				for (Node m : graph.getNodes()) {
					if(n.getIndex() < m.getIndex()){
						distances[n.getIndex()][m.getIndex()] = n.getPos().distance(m.getPos());
					}
				}
			}

			//calculate repulsive forces
			graph.getNodes().forEach(n ->{
				for (Node m : graph.getNodes()) {
					if (n.getIndex() < m.getIndex()) {
						double dist = getDistance(n, m);
						if(dist > 0 && dist < 2 * (coarserLevel + 1) * optDist) {
							double rf = -10 * Math.pow(optDist, 3) / ((dist * dist));
							repulsiveForces[n.getIndex()][m.getIndex()] = rf;
							repulsiveForces[m.getIndex()][n.getIndex()] = rf;
						}
					}
				}
			});

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

				//apply attractive force to node n
				for (Node m : n.getNeighbours()) {
					double dist = getDistance(n, m);
					if(dist > 0){
						double k = attractiveForces[n.getIndex()][m.getIndex()] / dist;
						force = force.add((m.getPos().subtract(n.getPos())).multiply(k));
					}
				}

				//apply repulsive force to node n
				for (Node m : graph.getNodes()) {
					double dist = getDistance(n, m);
					if(dist > 0 && dist < 2 * (coarserLevel + 1) * optDist) {
						double k = repulsiveForces[n.getIndex()][m.getIndex()] / dist;
						force = force.add((m.getPos().subtract(n.getPos())).multiply(k));
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
			Arrays.stream(nodeEnergies).forEach(d -> energy+=d);

			painter.sleep(1);

			if(layoutVM.isCoolingOn()) {
				stepSize = updateStepLength(energy, prevEnergy);
			}
		}
		super.repaint();

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
		graphCoarser.setPainter(painter);
	}
}
