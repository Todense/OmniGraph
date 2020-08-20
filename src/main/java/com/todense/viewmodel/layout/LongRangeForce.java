package com.todense.viewmodel.layout;

public enum LongRangeForce {
    WEAK("Weak", 3),
    NORMAL("Normal", 2),
    STRONG("Strong", 1);

    private final String name;
    private final int exponent;

    LongRangeForce(String name, int exponent){
        this.name = name;
        this.exponent = exponent;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getExponent() {
        return exponent;
    }
}
