package com.todense.viewmodel.algorithm.service;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.LayoutViewModel;
import com.todense.viewmodel.algorithm.AlgorithmService;
import javafx.geometry.Point2D;

public class DynamicLayoutService extends AlgorithmService {

	private LayoutViewModel layoutVM;

	private int coolingCounter = 0;

	double energy = Double.POSITIVE_INFINITY;

	private double[][] repulsiveForces;
	private double[][] attractiveForces;

	private double stepSize;

	private Point2D center;

    public DynamicLayoutService(Graph graph, LayoutViewModel layoutVM, Point2D center){
    	super(graph);
    	this.layoutVM = layoutVM;
    	this.center = center;
	}

	@Override
	protected void perform() throws InterruptedException {
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
	}

	public void forceDirectedLayout() throws InterruptedException {

		init();

		while(!super.isCancelled() &&  stepSize > layoutVM.getTolerance()) {

			int optDist = layoutVM.getOptDist();

			for (Edge e : graph.getEdges()){
				e.setLength();
			}

			for (Node n : graph.getNodes()) {
				for (Node m : graph.getNodes()) {
					if (n.getIndex() < m.getIndex()) {
						double dist = n.getPos().distance(m.getPos());
						if(dist > 0.0000001d) {
							double rf = -10 * Math.pow(optDist, 3) / ((dist * dist));
							repulsiveForces[n.getIndex()][m.getIndex()] = rf;
							repulsiveForces[m.getIndex()][n.getIndex()] = rf;
						}
					}
				}
			}

			for (Edge e : graph.getEdges()) {
				double af = Math.pow(e.getLength(), 2) / optDist;
				attractiveForces[e.getN1().getIndex()][e.getN2().getIndex()] = af;
				attractiveForces[e.getN2().getIndex()][e.getN1().getIndex()] = af;
			}

			double prevEnergy = energy;
			energy = 0;

			for (Node n : graph.getNodes()) {
				Point2D f = new Point2D(0, 0);
				for (Node m : n.getNeighbours()) {
					double dist = n.getPos().distance(m.getPos());
					if(dist > 0.0000001d){
						double k = attractiveForces[n.getIndex()][m.getIndex()] / dist;
						f = f.add((m.getPos().subtract(n.getPos())).multiply(k));
					}

				}
				for (Node m : graph.getNodes()) {
					double dist = n.getPos().distance(m.getPos());
					if(dist > 0.0000001d){
						double k = repulsiveForces[n.getIndex()][m.getIndex()] / (n.getPos().distance(m.getPos()));
						f = f.add((m.getPos().subtract(n.getPos())).multiply(k));
					}
				}

				if(layoutVM.isPullingOn()) {
					f = f.subtract(n.getPos().subtract(center).multiply(layoutVM.getPullStrength() / optDist));
				}

				energy += Math.pow(f.getX(), 2) + Math.pow(f.getY(), 2);

				if(!Double.isNaN(f.getX())){
					n.setPos(n.getPos().add(f.normalize().multiply(stepSize)));
				}
			}

			painter.sleep(1);


			if(layoutVM.isCoolingOn()) {
				stepSize = updateStepLength(energy, prevEnergy);
			}
		}
		painter.repaint();

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
