package com.todense.viewmodel.layout;

public enum LayoutAlgorithm {
    ADAPTIVE_COOLING("Adaptive Cooling Layout"),
    D3("D3 Layout");
    private final String name;

    LayoutAlgorithm(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
