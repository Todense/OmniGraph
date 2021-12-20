package com.todense.viewmodel.scope;

import com.todense.model.graph.Edge;
import com.todense.viewmodel.ants.Ant;
import com.todense.viewmodel.ants.AntColonyVariant;
import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class AntsScope implements Scope {

    private final List<Ant> ants = new ArrayList<>();

    ArrayList<Integer> gbCycle = new ArrayList<>(); //globally best cycle

    private double[][] pheromones;

    private double maxPheromone = 0;

    //PARAMETERS
    private final IntegerProperty antCountProperty = new SimpleIntegerProperty(5);
    private final IntegerProperty neighbourhoodSizeProperty = new SimpleIntegerProperty(15);
    private final IntegerProperty rankSizeProperty = new SimpleIntegerProperty(5); // number of ants in ranking that add pheromones
    private final DoubleProperty alphaProperty = new SimpleDoubleProperty(1.0); // pheromone influence
    private final DoubleProperty betaProperty = new SimpleDoubleProperty(2.0); // distance influence
    private final DoubleProperty evaporationProperty = new SimpleDoubleProperty(0.1);
    private final DoubleProperty exploitationStrengthProperty = new SimpleDoubleProperty(0.9); //exploitation strength
    private final DoubleProperty localEvaporationProperty = new SimpleDoubleProperty(0.1);

    //LOCAL SEARCH
    private final BooleanProperty with2OptProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty with3OptProperty = new SimpleBooleanProperty(false);

    //VISUALS
    private final BooleanProperty antsAnimationOnProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty showPheromonesProperty = new SimpleBooleanProperty(true);
    private final ObjectProperty<Color> antColorProperty = new SimpleObjectProperty<>(Color.rgb(200,0,0));
    private final ObjectProperty<Color> cycleColorProperty = new SimpleObjectProperty<>(Color.rgb(210,210,30));
    private final DoubleProperty antSize = new SimpleDoubleProperty(0.25);
    private final ObjectProperty<AntColonyVariant> algorithmProperty = new SimpleObjectProperty<>();


    public double getPheromone(Edge e){
        if(pheromones == null
                || e.getN1().getIndex() >= pheromones.length
                || e.getN2().getIndex() >= pheromones.length)
            return 0;
        return pheromones[e.getN1().getIndex()][e.getN2().getIndex()];
    }

    public void setPheromone(int i, int j, double amount){
        pheromones[i][j] = amount;
    }

    public double getPheromone(int i, int j){
        return pheromones[i][j];
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public Color getAntColor() {
        return antColorProperty.get();
    }

    public ObjectProperty<Color> antColorProperty() {
        return antColorProperty;
    }

    public double getAntSize() {
        return antSize.get();
    }

    public ObjectProperty<AntColonyVariant> algorithmProperty() {
        return algorithmProperty;
    }

    public int getAntCount() {
        return antCountProperty.get();
    }

    public IntegerProperty antCountProperty() {
        return antCountProperty;
    }

    public double getAlpha() {
        return alphaProperty.get();
    }

    public DoubleProperty alphaProperty() {
        return alphaProperty;
    }

    public double getBeta() {
        return betaProperty.get();
    }

    public DoubleProperty betaProperty() {
        return betaProperty;
    }

    public double getEvaporation() {
        return evaporationProperty.get();
    }

    public DoubleProperty evaporationProperty() {
        return evaporationProperty;
    }

    public double getExploitationStrength() {
        return exploitationStrengthProperty.get();
    }

    public DoubleProperty exploitationStrengthProperty() {
        return exploitationStrengthProperty;
    }

    public double getLocalEvaporation() {
        return localEvaporationProperty.get();
    }

    public DoubleProperty localEvaporationProperty() {
        return localEvaporationProperty;
    }

    public boolean isWith2Opt() {
        return with2OptProperty.get();
    }

    public BooleanProperty with2OptProperty() {
        return with2OptProperty;
    }

    public boolean isWith3Opt() {
        return with3OptProperty.get();
    }

    public BooleanProperty with3OptProperty() {
        return with3OptProperty;
    }

    public void setPheromones(double[][] pheromones) {
        this.pheromones = pheromones;
    }

    public ArrayList<Integer> getGbCycle() {
        return gbCycle;
    }

    public void setGbCycle(ArrayList<Integer> gbCycle) {
        this.gbCycle = gbCycle;
    }

    public boolean isAntsAnimationOn() {
        return antsAnimationOnProperty.get();
    }

    public BooleanProperty antsAnimationOnProperty() {
        return antsAnimationOnProperty;
    }

    public boolean isShowingPheromones() {
        return showPheromonesProperty.get();
    }

    public BooleanProperty showPheromonesProperty() {
        return showPheromonesProperty;
    }

    public Color getCycleColor() {
        return cycleColorProperty.get();
    }

    public ObjectProperty<Color> cycleColorProperty() {
        return cycleColorProperty;
    }

    public int getNeighbourhoodSize() {
        return neighbourhoodSizeProperty.get();
    }

    public IntegerProperty neighbourhoodSizeProperty() {
        return neighbourhoodSizeProperty;
    }

    public int getRankSize() {
        return rankSizeProperty.get();
    }

    public IntegerProperty rankSizeProperty() {
        return rankSizeProperty;
    }

    public double getMaxPheromone() {
        return maxPheromone;
    }

    public void setMaxPheromone(double maxPheromone) {
        this.maxPheromone = maxPheromone;
    }
}
