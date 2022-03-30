package com.todense.viewmodel.file.format;

public enum GraphFileFormat {
    OGR("OmniGraph Format", "ogr"),
    TSP("TSP format", "tsp"),
    MTX("Matrix market format", "mtx"),
    GRAPHML("GraphML format", "graphml"),
    SVG("SVG format", "svg");

    private final String name;
    private final String extension;

    GraphFileFormat(String name, String extension){
        this.name = name;
        this.extension = extension;
    }

    public final String toString(){
        return name+" (."+extension+")";
    }

    public String getExtension(){
        return extension;
    }
}

