package com.todense.viewmodel.preset;

public enum Preset {

    CYCLE("Cycle"),
    GRID("Grid"),
    KING("King's"),
    MAZE("Maze"),
    STAR("Star"),
    COMPLETE_BIPARTITE("Complete bipartite");

    private String name;

    Preset(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }


}
