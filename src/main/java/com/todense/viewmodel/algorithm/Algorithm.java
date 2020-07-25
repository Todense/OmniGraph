package com.todense.viewmodel.algorithm;

public enum Algorithm {
    DFS("DFS", true, false),
    BFS("BFS", true, false),
    DIJKSTRA("Dijkstra shortest path", true, true),
    ASTAR("A* shortest path", true, true),
    KRUSKAL("Kruskal MST", false, false),
    PRIM("Prim MST", true, false),
    HCSEARCH("Hamiltonian cycle search", true, false);

    private final String name;
    private final boolean withStart;
    private final boolean withGoal;

    Algorithm(String name, boolean withStart, boolean withGoal){
        this.name = name;
        this.withStart = withStart;
        this.withGoal = withGoal;
    }

    public boolean isWithStart() {
        return withStart;
    }

    public boolean isWithGoal() {
        return withGoal;
    }

    public String toString(){
        return name;
    }
}
