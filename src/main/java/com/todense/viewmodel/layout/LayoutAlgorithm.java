package com.todense.viewmodel.layout;

public enum LayoutAlgorithm {
    YIFAN_HU("Yifan Hu Algorithm"),
    D3("D3 Algorithm");
    private final String name;

    LayoutAlgorithm(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
