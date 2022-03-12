package com.todense.model;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class EdgeList extends ArrayList<Edge> {

    private final HashMap<String, Edge> edgeMap = new HashMap<>(); //nodes -> edge mapping

    @Override
    public boolean add(Edge edge) {
        edge.getN1().getNeighbours().add(edge.getN2());
        edge.getN2().getNeighbours().add(edge.getN1());
        edgeMap.put(edge.getId(), edge);
        return super.add(edge);
    }

    @Override
    public Edge remove(int index) {
        edgeMap.remove(this.get(index).getId());
        return super.remove(index);
    }

    public void remove(Node n, Node m){
        remove(getEdge(n, m));
    }

    @Override
    public boolean remove(Object o) {
        Edge e = (Edge)o;
        if(e == null || !edgeMap.containsKey(e.getId())){
            return false;
        }
        e.getN1().getNeighbours().remove(e.getN2());
        e.getN2().getNeighbours().remove(e.getN1());
        edgeMap.remove(e.getId());
        return super.remove(o);
    }

    @Override
    public void clear() {
        edgeMap.clear();
        super.clear();
    }


    public Edge getEdge(Node n, Node m){
        String id = n.getID() < m.getID() ?
                n.getID()+"-"+m.getID() :
                m.getID()+"-"+n.getID();
        return edgeMap.get(id);
    }

    public boolean isEdgeBetween(Node n, Node m){
        return n.getNeighbours().contains(m);
    }
}
