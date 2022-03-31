package com.todense.viewmodel.file.format.svg;

public enum SvgBackground {
    TRANSPARENT("Transparent"),
    WHITE("White"),
    ORIGINAL("Original");

    private final String name;

    SvgBackground(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
