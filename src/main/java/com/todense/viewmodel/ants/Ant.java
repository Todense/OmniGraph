package com.todense.viewmodel.ants;


import com.todense.viewmodel.algorithm.WalkingAgent;

import java.util.ArrayList;

public class Ant extends WalkingAgent {

    private double cycleLength = 0;

    private ArrayList<Integer> cycle = new ArrayList<>();

    private boolean[] dlb; // don't look bits
    private boolean[] visited;

    private int start;
    private int goal;
    private int previous;

    public Ant(int start){
        this.start = start;
    }

    public ArrayList<Integer> getCycle() {
        return cycle;
    }

    public void setCycle(ArrayList<Integer> cycle) {
        this.cycle = cycle;
    }

    public double getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(double cycleLength) {
        this.cycleLength = cycleLength;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean[] getDlb() {
        return dlb;
    }

    public void setDlb(boolean[] dlb) {
        this.dlb = dlb;
    }

    public boolean isVisited(int i){
        return visited[i];
    }

    public void setVisited(int i){
        visited[i] = true;
    }

    public void setVisited(boolean[] visited) {
        this.visited = visited;
    }

    public int getPrevious() {
        return previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }
}

