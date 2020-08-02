package com.todense.viewmodel.random.arrangement;

public enum NodeArrangement {
    RANDOM_CIRCLE("Uniformly in Circle"),
    RANDOM_SQUARE("Uniformly in Square"),
    CIRCULAR("Circular");

    private final String name;

    NodeArrangement(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
