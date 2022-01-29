package com.todense.viewmodel.layout;

public enum LayoutAlgorithm {
    D3("D3-Force Layout"),
    ADAPTIVE_COOLING("Adaptive Cooling Layout");
    private final String name;

    LayoutAlgorithm(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
