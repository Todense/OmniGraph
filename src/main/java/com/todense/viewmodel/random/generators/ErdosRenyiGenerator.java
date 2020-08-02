package com.todense.viewmodel.random.generators;

import com.todense.model.graph.Node;
import com.todense.viewmodel.random.RandomEdgeGenerator;

import java.util.List;

public class ErdosRenyiGenerator extends RandomEdgeGenerator {

    private double probability;

    public ErdosRenyiGenerator(List<Node> nodes, double probability){
        super(nodes);
        this.probability = probability;
    }

    @Override
    protected void generate() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < i; j++) {
                if(rnd.nextDouble() < probability){
                    super.addEdge(i, j);
                }
            }
        }
    }
}
