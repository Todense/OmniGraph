package com.todense.viewmodel.random.generators;

import com.todense.model.graph.Node;
import com.todense.viewmodel.random.RandomEdgeGenerator;

public class GeometricGenerator extends RandomEdgeGenerator {

    private final double threshold;
    private final boolean randomized;

    public GeometricGenerator(double threshold, boolean randomized) {
        super();
        this.threshold = threshold;
        this.randomized = randomized;
    }

    @Override
    protected void generate() {
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            for (int j = i+1; j < nodes.size(); j++) {
                Node m = nodes.get(j);
                double dist = n.getPos().distance(m.getPos());
                if(dist < threshold) {
                    if(randomized) {
                        double r =  Math.random() * threshold;
                        if (r < dist) {
                            super.addEdge(i, j);
                        }
                    }
                    else{
                        super.addEdge(i, j);
                    }
                }
            }
        }
    }
}
