package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.algorithm.AlgorithmService;
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

	private Point2D center;

    public DynamicLayoutService(Graph graph, LayoutViewModel layoutVM, Point2D center){
    	super(graph);
    	this.layoutVM = layoutVM;
    	this.center = center;
	}

	@Override
	public void perform() throws InterruptedException {
		forceDirectedLayout();
	}

	@Override
	protected void onFinished() {
	}

	void init(){
    	int nodeCount = graph.getNodes().size();
    	stepSize = layoutVM.getStep();
		repulsiveForces = new double[nodeCount][nodeCount];
		attractiveForces = new double[nodeCount][nodeCount];
		nodeEnergies = new double[nodeCount];
		distances = new double[nodeCount][nodeCount];
	}

	public int counter = 0;

	public void forceDirectedLayout() throws InterruptedException {

		init();

		while(!super.isCancelled() &&  stepSize > layoutVM.getTolerance()) {
			counter++;
			int optDist = layoutVM.getOptDist();

			for (Node n : graph.getNodes()) {
				for (Node m : graph.getNodes()) {
					if(n.getIndex() < m.getIndex()){
						distances[n.getIndex()][m.getIndex()] = n.getPos().distance(m.getPos());
					}
				}
			}

			graph.getNodes().forEach(n ->{
				for (Node m : graph.getNodes()) {
					if (n.getIndex() < m.getIndex()) {
						double dist = getDistance(n, m);
						if(dist > 0.0000001) {
							double rf = -10 * Math.pow(optDist, 3) / ((dist * dist));
							repulsiveForces[n.getIndex()][m.getIndex()] = rf;
							repulsiveForces[m.getIndex()][n.getIndex()] = rf;
						}
					}
				}
			});

			graph.getEdges().forEach(e ->{
				double af = Math.pow(getDistance(e.getN1(), e.getN2()), 2) / optDist;
				attractiveForces[e.getN1().getIndex()][e.getN2().getIndex()] = af;
				attractiveForces[e.getN2().getIndex()][e.getN1().getIndex()] = af;
			});

			double prevEnergy = energy;
			energy = 0;

			graph.getNodes().forEach(n ->{
				Point2D f = new Point2D(0, 0);
				for (Node m : n.getNeighbours()) {
					double dist = getDistance(n, m);
					if(dist > 0.0000001){
						double k = attractiveForces[n.getIndex()][m.getIndex()] / dist;
						f = f.add((m.getPos().subtract(n.getPos())).multiply(k));
					}
				}
				for (Node m : graph.getNodes()) {
					double dist = getDistance(n, m);
					if(dist > 0.0000001){
						double k = repulsiveForces[n.getIndex()][m.getIndex()] / dist;
						f = f.add((m.getPos().subtract(n.getPos())).multiply(k));
					}
				}

				if(layoutVM.isPullingOn()) {
					f = f.subtract(n.getPos().subtract(center).multiply(layoutVM.getPullStrength() / optDist));
				}
				nodeEnergies[n.getIndex()] = Math.pow(f.getX(), 2) + Math.pow(f.getY(), 2);
				//energy.addAndGet(Math.pow(f.getX(), 2) + Math.pow(f.getY(), 2));

				if(!Double.isNaN(f.getX())){
					n.setPos(n.getPos().add(f.normalize().multiply(stepSize)));
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
}
