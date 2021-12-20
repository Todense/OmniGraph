package com.todense.viewmodel.ants;

public enum AntColonyVariant {

    ACS("Ant colony system"),
    AS("Ant system"),
    MMAS("Max-min ant system"),
    RANK_AS("Ranked ant system");

    private final String name;

    AntColonyVariant(String name){
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
