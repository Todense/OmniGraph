package com.todense.viewmodel.file.format;

public enum Format {
    OGR("OmniGraph Format", "ogr"),
    TSP("TSP format", "tsp"),
    MTX("Matrix market format", "mtx"),
    GRAPHML("GraphML format", "graphml");

    private final String name;
    private final String extension;

    Format(String name, String extension){
        this.name = name;
        this.extension = extension;
    }

    public final String toString(){
        return name+"(."+extension+")";
    }

    public String getExtension(){
        return extension;
    }
}

