package com.todense.viewmodel.ants;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmTask;
import com.todense.viewmodel.scope.AlgorithmScope;
import com.todense.viewmodel.scope.AntsScope;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.GuideTableDiscreteSampler;
import org.apache.commons.rng.sampling.distribution.SharedStateDiscreteSampler;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AntColonyAlgorithmTask extends AlgorithmTask {

    public static final int EDGE_OUTSIDE_CYCLE = 0;
    public static final int EDGE_ON_CYCLE = 1;

    final AntsScope antsScope;
    final AlgorithmScope algorithmScope;

    private DoubleProperty bestSolutionLength = new SimpleDoubleProperty(Double.POSITIVE_INFINITY); //global best cycle length
    
    protected int graphOrder;
    
    private int iterationCounter = 0;
    private int explorationCounter = 0;
    private double explorationRate = 0;
    private double explorationRateSum = 0;

    double[][] dist;
    protected boolean[][] isImportant;

    double maxPheromone = 1;
    double minPheromone = 0.00001;

    private double maxTimeSec = Double.POSITIVE_INFINITY;
    private double minSolution = -1;
    private double maxIterations = Double.POSITIVE_INFINITY;

    private HashMap<Integer, List<Integer>> neighbourhoods = new HashMap<>();
    private LocalSearcher localSearcher = new LocalSearcher();
    private Random rnd = new Random();
    private final Object lock = new Object();
    UniformRandomProvider randomProvider;  //faster than EnumeratedDistribution

    public AntColonyAlgorithmTask(Graph graph, AntsScope antsScope, AlgorithmScope algorithmScope){
        super(graph);
        this.antsScope = antsScope;
        this.algorithmScope = algorithmScope;
        this.graphOrder = graph.getOrder();
        this.randomProvider = new RandomDoubleProvider();

        antsScope.getAnts().clear();
        antsScope.getGbCycle().clear();

        for (int i = 0; i < antsScope.getAntCount(); i++) {
            antsScope.getAnts().add(new Ant(rnd.nextInt(graphOrder)));
        }

        antsScope.setGbCycle(new ArrayList<>());
        antsScope.setPheromones(new double[graphOrder][graphOrder]);
        algorithmScope.setWalkingAgents(antsScope.getAnts());
    }

    @Override
    public void perform() {
        startTime = System.currentTimeMillis();
        antColonyOptimization();
        super.result = bestSolutionLength.get();
    }

    @Override
    protected void onFinished() {
    }

    protected double getInitialPheromoneLevel(){
        return minPheromone;
    }

    protected void init(){
        long initStart = System.currentTimeMillis();
        dist = new double[graphOrder][graphOrder];
        isImportant = new boolean[graphOrder][graphOrder];

        for(int i = 0; i < graphOrder; i++){
            Node n1 = graph.getNodes().get(i);
            for(int j = 0; j < graphOrder; j++){
                Node n2 = graph.getNodes().get(j);
                dist[i][j] = n1.getPos().distance(n2.getPos());
            }
        }

        double initialPheromoneLevel = getInitialPheromoneLevel();

        for(int i = 0; i < graphOrder; i++){
            for(int j = 0; j < graphOrder; j++){
                antsScope.setPheromone(i, j , initialPheromoneLevel);
            }
        }

        setNeighbourhoods(antsScope.getNeighbourhoodSize());

        for (int i = 0; i < this.graphOrder; i++) {
            for (int j = 0; j < this.graphOrder; j++) {
                if(neighbourhoods.get(i).contains(j)){
                    isImportant[i][j] = true;
                    isImportant[j][i] = true;
                }
            }
        }

        for(Ant ant : antsScope.getAnts()){
            ant.setVisited(new boolean[graphOrder]);
        }

        for (Edge e : graph.getEdges()){
            int i = e.getN1().getIndex();
            int j = e.getN2().getIndex();
            e.setStatus(EDGE_OUTSIDE_CYCLE);
            e.setVisible(isImportant[i][j]);
        }
    }

    //main algorithm
    private void antColonyOptimization(){
        init();
        while(!super.isCancelled() && !endConditionsSatisfied()){
            iterationCounter++;
            for (int i = 0; i < graphOrder; i++) {
                moveAnts();
            }
            localSearch();
            checkIterationResults();
            updatePheromones();
            setMaxPheromone();
            resetAnts();
        }
    }



    private void localSearch() {
        if(antsScope.isWith3Opt()){
            for(Ant ant : antsScope.getAnts()) {
                double oldLength = Double.POSITIVE_INFINITY;
                while (oldLength > ant.getCycleLength()) {
                    oldLength = ant.getCycleLength();
                    localSearcher.threeOpt(ant, graphOrder, dist, isImportant);
                }
            }
        }
        else if(antsScope.isWith2Opt()){
            for(Ant ant : antsScope.getAnts()) {
                ant.setDlb(new boolean[graphOrder]);
                double oldLength = Double.POSITIVE_INFINITY;
                while (oldLength > ant.getCycleLength()) {
                    oldLength = ant.getCycleLength();
                    localSearcher.twoOpt(ant, graphOrder, dist, isImportant);
                }
            }
        }
    }

    protected void moveAnts() {
        AtomicBoolean working = new AtomicBoolean(true);
        for (Ant ant : antsScope.getAnts()) {
            ant.setPrevious(ant.getStart());
            moveAnt(ant);
        }
        if(!super.isConnectedToUI())
            return;
        if(painter.isAnimationOn() && antsScope.isAntsAnimationOn()){
            ParallelTransition transition = new ParallelTransition();
            for (Ant ant : antsScope.getAnts()) {
                transition.getChildren().add(antMoveTimeline(ant));
            }
            transition.setOnFinished(actionEvent ->{
                working.set(false);
                synchronized (lock) {
                    lock.notifyAll();
                }
            });
            transition.play();
            while (working.get()) {
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        working.set(false);
                        transition.stop();
                        antsScope.getAnts().clear();
                    }
                }
            }
        }
    }
    
    protected void moveAnt(Ant ant){
        ant.getCycle().add(ant.getStart());
        ant.setVisited(ant.getStart());

        if(ant.getCycle().size() == graphOrder){
            ant.setGoal(ant.getCycle().get(0));
        }
        else{
            ant.setGoal(getRandomNeighbour(ant, getCurrentNeighbours(ant)));
        }

        if(!isInCurrentBestSolution(ant.getStart(), ant.getGoal())) {
            explorationCounter++;
        }

        ant.setCycleLength(ant.getCycleLength() + dist[ant.getStart()][ant.getGoal()]);

        if(ant.getCycle().size() == graphOrder){
            ant.setStart(rnd.nextInt(graphOrder-1)); //random start node for new cycle
        }
        else{
            ant.setStart(ant.getGoal());
        }
    }

    protected int getRandomNeighbour(Ant ant, List<Integer> availableNeighbours) {

        int start = ant.getStart();

        double[] probabilities = new double[availableNeighbours.size()];
        int j = 0;
        for (int i: availableNeighbours) {
            double ph = getPheromone(start, i);
            double dist = this.dist[start][i];
            probabilities[j++] = Math.pow(ph, antsScope.getAlpha()) * Math.pow(1/dist, antsScope.getBeta());
        }
        SharedStateDiscreteSampler distribution = GuideTableDiscreteSampler.of(randomProvider, probabilities);
        return availableNeighbours.get(distribution.sample());
    }

    private List<Integer> getCurrentNeighbours(Ant ant){
        ArrayList<Integer> currentNeighbours = new ArrayList<>();

        for (int i: neighbourhoods.get(ant.getStart())) {
            if(!ant.isVisited(i)){
                currentNeighbours.add(i);
            }
        }

        if(currentNeighbours.size() == 0){ // all nodes in neighbourhood are already visited
            currentNeighbours = IntStream.range(0, graphOrder)
                    .boxed()
                    .filter(integer -> !ant.getCycle().contains(integer))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return currentNeighbours;
    }


    protected void updatePheromones() {
        evaporatePheromone(antsScope.getEvaporation());
    }

    protected void setMaxPheromone(){
        double maxPheromone = graph.getEdges().stream().mapToDouble(antsScope::getPheromone).max().getAsDouble();
        antsScope.setMaxPheromone(maxPheromone);
    }

    protected void evaporatePheromone(double evaporation){
        for (int i = 0; i < graphOrder; i++) {
            for (int j = i + 1; j < graphOrder; j++) {
                setPheromone(i, j, getPheromone(i, j) * (1 - evaporation));
            }
        }
    }

    protected void addPheromoneToGlobalBestCycle(double multiplier){
        for (int i = 0; i < antsScope.getGbCycle().size(); i++) {
            int i1 = antsScope.getGbCycle().get(i);
            int i2 = antsScope.getGbCycle().get((i + 1) % graphOrder);
            addPheromone(i1, i2, multiplier / bestSolutionLength.get());
        }
    }

    protected void checkIterationResults() {
        Ant iterationBestAnt = getIterationBestAnt();

        if(iterationBestAnt == null){
            return;
        }

        if (iterationBestAnt.getCycleLength() + 0.0001 < bestSolutionLength.get()) {
            antsScope.setGbCycle(new ArrayList<>(iterationBestAnt.getCycle()));
            bestSolutionLength.set(iterationBestAnt.getCycleLength());
            updateCycleMarkers();
        }

        explorationRate = (double)explorationCounter/(graphOrder * antsScope.getAntCount());
        explorationRateSum += explorationRate;
        explorationCounter = 0;
    }


    protected Ant getIterationBestAnt() {
        Ant ibAnt  = null;
        double length = Double.POSITIVE_INFINITY;
        for (Ant a : antsScope.getAnts()) {
            if (a.getCycleLength() + 0.0001 < length) {
                length = a.getCycleLength();
                ibAnt = a;
            }
        }
        return ibAnt;
    }

    protected void resetAnts() {
        for(Ant ant : antsScope.getAnts()){
            ant.getCycle().clear();
            ant.setCycleLength(0);
            ant.setVisited(new boolean[graphOrder]);
        }
    }

    private boolean endConditionsSatisfied() {
        return System.currentTimeMillis() - startTime > 1000 * maxTimeSec ||
                bestSolutionLength.get() <= minSolution ||
                iterationCounter > maxIterations;
    }

    private boolean isInCurrentBestSolution(int j1, int j2){
        for (int i = 0; i < antsScope.getGbCycle().size(); i++) {
            int i1 = antsScope.getGbCycle().get(i);
            int i2 = antsScope.getGbCycle().get((i + 1) % graphOrder);
            if((j1 == i1 && j2 == i2) || (j1 == i2 && j2 == i1))
                return true;
        }
        return false;
    }

    private void setNeighbourhoods(int neighbourhoodSize){
        for(int i = 0; i < graphOrder; i++){
            List<Integer> neighbourhood = new ArrayList<>();
            for (int j = 0; j < graphOrder; j++) {
                if(j != i){
                    neighbourhood.add(j);
                }
            }
            Point2D pt  = graph.getNodes().get(i).getPos();
            neighbourhood.sort((o1, o2) ->{
                double d1 = pt.distance(graph.getNodes().get(o1).getPos());
                double d2 = pt.distance(graph.getNodes().get(o2).getPos());
                if(d1 == d2) return 0;
                return d1 < d2 ? -1 : 1;
            });
            if(neighbourhoodSize < graphOrder-1) {
                neighbourhood = neighbourhood.subList(0, neighbourhoodSize);
            }
            neighbourhoods.put(i, neighbourhood);
        }
    }

    private void updateCycleMarkers(){
        for (Edge e : graph.getEdges()) {
            e.setStatus(EDGE_OUTSIDE_CYCLE);
        }

        if(antsScope.getGbCycle() != null) {
            for (int i = 0; i < antsScope.getGbCycle().size(); i++) {
                Edge e = graph.getEdge(
                        antsScope.getGbCycle().get(i),
                        antsScope.getGbCycle().get((i + 1) % graphOrder)
                );
                e.setStatus(EDGE_ON_CYCLE);
            }
        }
    }

    void setPheromone(int i, int j, double amount){
        antsScope.setPheromone(i, j, inPheromoneRange(amount));
        antsScope.setPheromone(j, i, inPheromoneRange(amount));
    }

    void addPheromone(int i, int j, double amount){
        double ph =  antsScope.getPheromone(i, j);
        antsScope.setPheromone(i, j, inPheromoneRange(ph + amount));
        antsScope.setPheromone(j, i, inPheromoneRange(ph + amount));
    }

    double getPheromone(int i, int j){
        return antsScope.getPheromone(i, j);
    }

    private double inPheromoneRange(double value) {
        return Math.min(Math.max(value, minPheromone), maxPheromone);
    }

    private Timeline antMoveTimeline(Ant ant) {
        Node start = graph.getNodes().get(ant.getPrevious());
        Node goal = graph.getNodes().get(ant.getGoal());
        return new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(ant.xProperty(), start.getPos().getX()),
                        new KeyValue(ant.yProperty(), start.getPos().getY())
                ),
                new KeyFrame(Duration.millis(painter.getStepTime()),
                        new KeyValue(ant.xProperty(), goal.getPos().getX()),
                        new KeyValue(ant.yProperty(), goal.getPos().getY())
                )
        );
    }

    public double getBestSolutionLength() {
        return bestSolutionLength.get();
    }

    public DoubleProperty bestSolutionLengthProperty() {
        return bestSolutionLength;
    }


    public void setMaxTimeSec(double maxTimeSec) {
        this.maxTimeSec = maxTimeSec;
    }

    public void setMinSolution(double minSolution) {
        this.minSolution = minSolution;
    }

    public void setMaxIterations(double maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getIterationCounter() {
        return iterationCounter;
    }

    public double getExplorationRate() {
        return explorationRate;
    }
}
