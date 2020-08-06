package com.todense.viewmodel.random.generators;

import com.todense.model.graph.Node;
import com.todense.viewmodel.random.RandomEdgeGenerator;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;

public class BarabasiAlbertGenerator extends RandomEdgeGenerator {

    private final int m0;
    private final int m;

    public BarabasiAlbertGenerator(int m0, int m) {
        super();
        this.m0 = m0;
        this.m = m;
    }

    @Override
    protected void generate() {
        for (int i = 0; i < m0-1; i++) {
            super.addEdge(i, i+1);
        }

        for (int i = m0; i < nodes.size(); i++) {
            Node current = super.nodes.get(i);
            for (int j = 0; j < m; j++) {
                ArrayList<Pair<Integer, Double>> probabilities = new ArrayList<>();
                for(Node k : nodes){
                    if(k.getIndex() < current.getIndex() && !current.getNeighbours().contains(k)) {
                        probabilities.add(new Pair<>(k.getIndex(), (double) k.getNeighbours().size()+1));
                    }
                }
                if(probabilities.size() > 0) {
                    EnumeratedDistribution<Integer> distribution = new EnumeratedDistribution<>(probabilities);
                    int k = distribution.sample();
                    super.addEdge(i, k);
                }
            }
        }
    }
}
