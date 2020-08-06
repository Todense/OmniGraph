package com.todense.model;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class EdgeList extends ArrayList<Edge> {

    private HashMap<String, Edge> edgeMap = new HashMap<>(); //edgeID -> edge mapping

    @Override
    public boolean add(Edge edge) {
        edge.getN1().getNeighbours().add(edge.getN2());
        edge.getN2().getNeighbours().add(edge.getN1());
        edgeMap.put(edge.getID(), edge);
        return super.add(edge);
    }

    @Override
    public Edge remove(int index) {
        edgeMap.remove(this.get(index).getID());
        return super.remove(index);
    }

    public boolean remove(Node n, Node m){
       return remove(getEdge(n, m));
    }

    @Override
    public boolean remove(Object o) {
        Edge e = (Edge)o;
        e.getN1().getNeighbours().remove(e.getN2());
        e.getN2().getNeighbours().remove(e.getN1());
        edgeMap.remove(e.getID());
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
        assert edgeMap.get(id) != null : "No edge with id: "+id;
        return edgeMap.get(id);
    }

    public boolean isEdgeBetween(Node n, Node m){
        String id = n.getID() < m.getID() ?
                n.getID()+"-"+m.getID() :
                m.getID()+"-"+n.getID();
        return edgeMap.containsKey(id);
    }
}
