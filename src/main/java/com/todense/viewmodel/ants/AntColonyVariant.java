package com.todense.viewmodel.ants;

public enum AntColonyVariant {

    ACS("Ant colony system", true, false, true, true, false, false),
    AS("Ant system",false, false,false, false, false, false),
    MMAS("Max-min ant system", true, false,false, false, true, true),
    RANK_AS("Ranked ant system", true, true,false, false, false, false);

    private final String name;
    private final boolean gbAnt;
    private final boolean ranked;
    private final boolean withQ0;
    private final boolean localUpdate;
    private final boolean minMax;
    private final boolean initMaxPh;

    AntColonyVariant(String name, boolean gbAnt, boolean ranked, boolean withQ0, boolean localUpdate, boolean minMax, boolean initMaxPh){
        this.name = name;
        this.gbAnt = gbAnt;
        this.ranked = ranked;
        this.withQ0 = withQ0;
        this.localUpdate = localUpdate;
        this.minMax = minMax;
        this.initMaxPh = initMaxPh;
    }

    public boolean isGbAnt() {
        return gbAnt;
    }

    public boolean isRanked() {
        return ranked;
    }

    public boolean isWithQ0() {
        return withQ0;
    }

    public boolean withLocalUpdate() {
        return localUpdate;
    }

    public boolean isMinMax() {
        return minMax;
    }

    public boolean isInitMaxPh() {
        return initMaxPh;
    }

    public String toString() {
        return name;
    }
}
