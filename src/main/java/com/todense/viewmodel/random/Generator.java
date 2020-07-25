package com.todense.viewmodel.random;

public enum Generator {

    GEOMETRIC("Geometric"),
    GEOMETRIC_RANDOMIZED("Geometric Randomized"),
    ERDOS_RENYI("Erdős–Rényi model"),
    BARABASI_ALBERT("Barabási–Albert model");

    private final String name;

    Generator(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
