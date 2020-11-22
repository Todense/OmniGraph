package com.todense.viewmodel.ants;

import java.util.ArrayList;

public class LocalSearcher {

    public void twoOpt(Ant ant, int graphOrder, double[][] dist, boolean[][] isImportant){
        for (int i = 0; i < graphOrder; i++) {
            if(!ant.getDlb()[i]) {
                int n = ant.getCycle().get(i);
                int n_nxt = ant.getCycle().get((i + 1) % graphOrder);
                double r = dist[n][n_nxt];
                for (int j = i + 2; j < graphOrder; j++) {
                    int m = ant.getCycle().get(j);
                    if (isImportant[n][m] && (dist[n][m] < r || dist[n_nxt][n_nxt] < r)) {
                        int m_nxt = ant.getCycle().get((j + 1)%graphOrder);
                        if (isImportant[n_nxt][m_nxt]) {
                            double oldLength = dist[n][n_nxt] + dist[m][m_nxt];
                            double newLength = dist[n][m] + dist[n_nxt][m_nxt];
                            if (newLength < oldLength) {
                                ant.getDlb()[i]
                                        = ant.getDlb()[(i+1)%graphOrder]
                                        = ant.getDlb()[j%graphOrder]
                                        = ant.getDlb()[(j+1)%graphOrder]
                                        = false;

                                ant.setCycle(swap2Edges(
                                        ant.getCycle(), ant.getCycle().indexOf(n),
                                        ant.getCycle().indexOf(m))
                                );
                                ant.setCycleLength(ant.getCycleLength() - (oldLength - newLength));
                                return;
                            }
                        }
                    }
                }
            }
            ant.getDlb()[i] = true;
        }
    }

    public void threeOpt(Ant ant, int graphOrder, double[][] dist, boolean[][] isImportant){
        for(int i = 0; i < graphOrder; i++){
            for (int j = i+2; j < graphOrder; j++) {
                for (int k = j+2; k < graphOrder-(i==0? 1 : 0); k++) {

                    int x = ant.getCycle().get(i);
                    int y = ant.getCycle().get(j);
                    int z = ant.getCycle().get(k);

                    int x2 = ant.getCycle().get((i+1)%graphOrder);
                    int y2 = ant.getCycle().get((j+1)%graphOrder);
                    int z2 = ant.getCycle().get((k+1)%graphOrder);

                    if(isImportant[x][y] && isImportant[y][z]){
                        for (int optCase = 7; optCase >= 1; optCase--) {
                            double oldLength = dist[x][x2] + dist[y][y2] + dist[z][z2];
                            double newLength = newEdgesLength(ant.getCycle(), i, j, k, optCase, graphOrder, dist);
                            if(newLength < oldLength){
                                ant.setCycle(swap3Edges(ant.getCycle(), i, j, k, optCase));
                                ant.setCycleLength(ant.getCycleLength() - (oldLength - newLength));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private double newEdgesLength(ArrayList<Integer> cycle, int i1, int i2, int i3, int optCase, int graphOrder, double[][] dist){
        int n = cycle.get(i1);
        int m = cycle.get(i2);
        int k = cycle.get(i3);
        int n_next = cycle.get((i1 + 1) % graphOrder);
        int m_next = cycle.get((i2 + 1) % graphOrder);
        int k_next = cycle.get((i3 + 1) % graphOrder);

        if(optCase == 1){
            return dist[n][k] + dist[n_next][k_next] + dist[m][m_next];
        }
        else if(optCase == 2){
            return dist[n][n_next] + dist[m][k] + dist[m_next][k_next];
        }
        else if(optCase == 3){
            return dist[n][m] + dist[m_next][n_next] + dist[k][k_next];
        }
        else if(optCase == 4){
            return dist[n][m] + dist[m_next][k_next] + dist[k][n_next];
        }
        else if(optCase == 5){
            return dist[n][k] + dist[n_next][m_next] + dist[m][k_next];
        }
        else if(optCase == 6){
            return dist[n][m_next] + dist[m][k] + dist[k_next][n_next];
        }
        else if(optCase == 7){
            return dist[n][m_next] + dist[m][k_next] + dist[k][n_next];
        }
        else
            return 0;
    }

    private ArrayList<Integer> swap2Edges(ArrayList<Integer> cycle, int n1, int n2){
        ArrayList<Integer> newCycle = new ArrayList<>();
        for (int j = 0; j <= n1 ; j++) {
            newCycle.add(cycle.get(j));
        }
        for (int j = n2; j > n1; j--) {
            newCycle.add(cycle.get(j));
        }
        for (int j = n2 + 1; j < cycle.size(); j++) {
            newCycle.add(cycle.get(j));
        }
        return  newCycle;
    }

    private ArrayList<Integer> swap3Edges(ArrayList<Integer> cycle, int n1, int n2, int n3, int optCase){
        ArrayList<Integer> newCycle = new ArrayList<>();

        if(optCase == 1){
            newCycle = swap2Edges(cycle, n1, n3);
        }
        else if(optCase == 2){
            newCycle = swap2Edges(cycle, n2, n3);
        }
        else if(optCase == 3){
            newCycle = swap2Edges(cycle, n1, n2);
        }
        else if(optCase == 4){
            for (int i = 0; i <= n1 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2; i > n1 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3; i > n2 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size() ; i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 5){
            for (int i = 0; i <= n1 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3; i > n2 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n1 + 1; i <= n2 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size() ; i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 6){
            for (int i = 0; i <= n1; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2 + 1; i <= n3; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2; i > n1; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size(); i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 7){
            for (int i = 0; i <= n1; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2 + 1; i <= n3; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n1 + 1; i <= n2; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size(); i++) {
                newCycle.add(cycle.get(i));
            }
        }
        return  newCycle;
    }

}
