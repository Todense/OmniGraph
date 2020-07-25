package com.todense.viewmodel.ants;

import com.todense.model.graph.Edge;
import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.algorithm.AlgorithmService;
import com.todense.viewmodel.scope.AntsScope;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AntColonyService extends AlgorithmService {

    private double maxPheromone = 1;
    private double minPheromone = 0.00001;

    private DoubleProperty bgLength = new SimpleDoubleProperty(Double.POSITIVE_INFINITY); //best global length
    
    private boolean[][] isImportant;
    private double[][] dist;

    private HashMap<Integer, List<Integer>> neighbourhoods = new HashMap<>();

    private Graph graph;
    private AntsScope antsScope;
    private AntColonyAlgorithm algorithm;
    
    private int nodeCount;

    private Random rnd = new Random();
    private final Object lock = new Object();

    public AntColonyService(AntColonyAlgorithm algorithm, AntsScope antsScope, Graph graph){
        super(graph);
        this.graph = graph;
        this.nodeCount = graph.getNodes().size();
        this.antsScope = antsScope;
        this.algorithm = algorithm;

        antsScope.getAnts().clear();
        antsScope.getGbCycle().clear();

        for (int i = 0; i < antsScope.getAntCount(); i++) {
            antsScope.getAnts().add(new Ant(rnd.nextInt(nodeCount)));
        }

        antsScope.setGbCycle(new ArrayList<>());
        antsScope.setPheromones(new double[nodeCount][nodeCount]);
    }

    @Override
    protected void perform() throws InterruptedException {
        antColonyOptimization(this.algorithm);
    }

    @Override
    protected void onFinished() {
        antsScope.getAnts().clear();
    }

    private void antColonyOptimization(AntColonyAlgorithm algorithm) throws InterruptedException {

        init(algorithm.isInitMaxPh());

        int counter = 0;

        while(!super.isCancelled()) {
            counter++;
            moveAnts(algorithm);
            if (counter == nodeCount) { //ants have completed cycles
                counter = 0;
                checkIterationResults(algorithm);
                updatePheromones(algorithm, antsScope.getEvaporation());

                for(Ant ant : antsScope.getAnts()){
                    ant.getCycle().clear();
                    ant.setCycleLength(0);
                    ant.setVisited(new boolean[nodeCount]);
                }

                if(algorithm.isMinMax()) {
                    maxPheromone = (1 / antsScope.getEvaporation()) * (1 / bgLength.get());
                    minPheromone = maxPheromone / (2 * nodeCount);
                }
            }
        }
        antsScope.getAnts().clear();
    }
    
    private void checkIterationResults(AntColonyAlgorithm algorithm){
        Ant iterationBestAnt;

        if(algorithm.isRanked()){
            antsScope.getAnts().sort(Comparator.comparingDouble(Ant::getCycleLength));
            iterationBestAnt = antsScope.getAnts().get(0);
        }
        else{
            iterationBestAnt = getIterationBestAnt();
        }

        if (iterationBestAnt.getCycleLength() + 0.0001 < bgLength.get()) {
            antsScope.setGbCycle(new ArrayList<>(iterationBestAnt.getCycle()));
            bgLength.set(iterationBestAnt.getCycleLength());
            updateCycleMarkers();
        }


    }
    
    private void updatePheromones(AntColonyAlgorithm algorithm, double evaporation){
        evaporatePheromone(evaporation);

        if(algorithm.isGbAnt()){
            if(algorithm.isRanked()){
                addPheromoneToGlobalBestCycle(antsScope.getRankSize());
            }
            else{
                addPheromoneToGlobalBestCycle(1);
            }
        }

        if(algorithm.isRanked()){
            addPheromoneToIterationBestCycles(antsScope.getRankSize());
        }
    }

    private void init(boolean initMaxPheromone){

        dist = new double[nodeCount][nodeCount];
        isImportant = new boolean[nodeCount][nodeCount];

        for(int i = 0; i < nodeCount; i++){
            Node n1 = graph.getNodes().get(i);
            for(int j = 0; j < nodeCount; j++){
                Node n2 = graph.getNodes().get(j);
                dist[i][j] = n1.getPos().distance(n2.getPos());
            }
        }
        if(!algorithm.isMinMax() && algorithm.withLocalUpdate()){
            double nnLength = getNearestNeighbourSearchCycleLength();
            minPheromone = 1/(nodeCount * nnLength);
        }

        double pheromoneStartLevel = initMaxPheromone? maxPheromone : minPheromone;

        for(int i = 0; i < nodeCount; i++){
            for(int j = 0; j < nodeCount; j++){
                antsScope.setPheromone(i, j , pheromoneStartLevel);
            }
        }

        setNeighbourhoods(antsScope.getNeighbourhoodSize());

        for (int i = 0; i < this.nodeCount; i++) {
            for (int j = 0; j < this.nodeCount; j++) {
                if(neighbourhoods.get(i).contains(j)){
                    isImportant[i][j] = true;
                }
            }
        }

        for(Ant ant : antsScope.getAnts()){
            ant.setVisited(new boolean[nodeCount]);
        }

        for (Edge e : graph.getEdges()){
            e.setMarked(false);
            e.setVisible(isImportant[e.getN1().getIndex()][e.getN2().getIndex()]);
        }
    }

    private void moveAnts(AntColonyAlgorithm algorithm) throws InterruptedException {
        AtomicBoolean working = new AtomicBoolean(true);

        for (Ant ant : antsScope.getAnts()) {
            ant.setPrevious(ant.getStart());
            moveAntRandom(ant, algorithm);
        }

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
                    lock.wait();
                }
            }
        }
    }

    private void moveAntRandom(Ant ant, AntColonyAlgorithm algorithm){
        ant.getCycle().add(ant.getStart());
        ant.setVisited(ant.getStart());

        if(ant.getCycle().size() == nodeCount){
            ant.setGoal(ant.getCycle().get(0));
        }
        else{
            ant.setGoal(getRandomNeighbour(ant, algorithm.isWithQ0()));
        }

        ant.setCycleLength(ant.getCycleLength() + dist[ant.getStart()][ant.getGoal()]);

        if(algorithm.withLocalUpdate()){
                setPheromone(
                        ant.getStart(),
                        ant.getGoal(),
                        (1 - antsScope.getLocalEvaporation()) * getPheromone(ant.getStart(), ant.getGoal()) +
                                antsScope.getLocalEvaporation() * minPheromone
                );
        }

        if(ant.getCycle().size() == nodeCount){

            if(antsScope.isWith3Opt()){
                double oldLength = Double.POSITIVE_INFINITY;
                while(oldLength > ant.getCycleLength()){
                    oldLength = ant.getCycleLength();
                    three_opt(ant);
                }
            }
            else if(antsScope.isWith2Opt()) {
                ant.setDlb(new boolean[nodeCount]);
                double oldLength = Double.POSITIVE_INFINITY;
                while(oldLength > ant.getCycleLength()){
                    oldLength =  ant.getCycleLength();
                    two_opt(ant);
                }
            }

            if(!algorithm.isGbAnt()){
                addPheromoneToAntCycle(ant);
            }
            ant.setStart(rnd.nextInt(nodeCount-1)); //random start node for new cycle
        }
        else{
            ant.setStart(ant.getGoal());
        }
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

    private double getNearestNeighbourSearchCycleLength(){
        double length = 0;
        boolean[] visited = new boolean[graph.getNodes().size()];
        Node n = graph.getNodes().get(0);
        while(true) {
            visited[n.getIndex()] = true;
            double minLength = Double.POSITIVE_INFINITY;
            Node minLengthNode = null;
            for (Node m : n.getNeighbours()) {
                if (!visited[m.getIndex()]) {
                    if (dist[n.getIndex()][m.getIndex()] < minLength) {
                        minLength = dist[n.getIndex()][m.getIndex()];
                        minLengthNode = m;
                    }
                }
            }
            if (minLengthNode != null) {
                length += minLength;
                visited[minLengthNode.getIndex()] = true;
                n = minLengthNode;
            } else {
                length += dist[n.getIndex()][graph.getNodes().get(0).getIndex()];
                break;
            }
        }

        return length;
    }

    private int getRandomNeighbour(Ant ant, boolean withQ0) throws MathArithmeticException {

        int start = ant.getStart();

        ArrayList<Pair<Integer, Double>> probabilities = new ArrayList<>();

        ArrayList<Integer> currentNeighbours = new ArrayList<>();

        for (int i : neighbourhoods.get(start)) {
            if(!ant.isVisited(i)){
                currentNeighbours.add(i);
            }
        }

        if(currentNeighbours.size() == 0){
            currentNeighbours = IntStream.range(0, nodeCount).boxed().collect(Collectors.toCollection(ArrayList::new));
            currentNeighbours.removeAll(ant.getCycle());
        }

        if(withQ0){
            if (rnd.nextDouble() < antsScope.getExploitationStrength()) {
                double bestQuality = 0;
                int bestNode = 0;
                for (Integer i : currentNeighbours) {
                    double dist = this.dist[start][i];
                    double edgeQuality = Math.pow(getPheromone(start, i), antsScope.getAlpha())
                            * Math.pow(1 / dist, antsScope.getBeta());
                    if (edgeQuality >= bestQuality) {
                        bestQuality = edgeQuality;
                        bestNode = i;
                    }
                }
                return bestNode;
            }
        }

        for(Integer i: currentNeighbours) {
            double ph = getPheromone(start, i);
            double dist = this.dist[start][i];
            probabilities.add(Pair.create(i, (Math.pow(ph, antsScope.getAlpha()) * Math.pow(1/dist, antsScope.getBeta()))));
        }

        EnumeratedDistribution<Integer> distribution = new EnumeratedDistribution<>(probabilities);

        return distribution.sample();
    }

    private Ant getIterationBestAnt(){
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

    private void evaporatePheromone(double evaporation){
        for (int i = 0; i < nodeCount; i++) {
            for (int j = i + 1; j < nodeCount; j++) {
                setPheromone(i, j, getPheromone(i, j) * (1 - evaporation));
            }
        }
    }

    private void addPheromoneToAntCycle(Ant ant){
        for (int i = 0; i < ant.getCycle().size(); i++) {
            int i1 = ant.getCycle().get(i);
            int i2 = ant.getCycle().get((i + 1) % nodeCount);
            addPheromone(i1, i2, 1/ant.getCycleLength());
        }
    }

    private void addPheromoneToGlobalBestCycle(double multiplier){
        for (int i = 0; i < antsScope.getGbCycle().size(); i++) {
            int i1 = antsScope.getGbCycle().get(i);
            int i2 = antsScope.getGbCycle().get((i + 1) % nodeCount);
            addPheromone(i1, i2, multiplier / bgLength.get());
        }
    }

    private void addPheromoneToIterationBestCycles(int rankNumber){

        for(int i = 1; i <= rankNumber; i++){
            Ant a = antsScope.getAnts().get(i-1);
            for (int j = 0; j < a.getCycle().size(); j++) {
                int i1 = a.getCycle().get(j);
                int i2 = a.getCycle().get((j + 1) % nodeCount);
                if(isImportant[i1][i2]) {
                    addPheromone(i1, i2, (rankNumber - i) / a.getCycleLength());
                }
            }
        }
    }

    private void setNeighbourhoods(int neighbourhoodSize){
        for(int i = 0; i < nodeCount; i++){
            List<Integer> neighbourhood = new ArrayList<>();
            for (int j = 0; j < nodeCount; j++) {
                if(j != i){
                    neighbourhood.add(j);
                }
            }

            Point2D pt  = graph.getNodes().get(i).getPos();
            neighbourhood.sort((o1, o2) ->
                    (int) (pt.distance(graph.getNodes().get(o1).getPos()) -
                            pt.distance(graph.getNodes().get(o2).getPos())));

            if(neighbourhoodSize < nodeCount-1) {
                neighbourhoods.put(i, neighbourhood.subList(0, neighbourhoodSize));
            }else{
                neighbourhoods.put(i, neighbourhood);
            }

        }
    }

    private void updateCycleMarkers(){

        for (Edge e : graph.getEdges()) {
            e.setMarked(false);
        }

        if(antsScope.getGbCycle() != null) {
            for (int i = 0; i < antsScope.getGbCycle().size(); i++) {
                Edge e = graph.getEdge(antsScope.getGbCycle().get(i), antsScope.getGbCycle().get((i + 1) % nodeCount));
                e.setMarked(true);
            }
        }
    }

    private void setPheromone(int i, int j, double amount){
        antsScope.setPheromone(i, j, inPheromoneRange(amount));
        antsScope.setPheromone(j, i, inPheromoneRange(amount));
    }

    private void addPheromone(int i, int j, double amount){
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
    
    private void two_opt(Ant ant){
        for (int i = 0; i < nodeCount; i++) {
            if(!ant.getDlb()[i]) {
                int n = ant.getCycle().get(i);
                int n_nxt = ant.getCycle().get((i + 1) % nodeCount);
                double r = dist[n][n_nxt];
                for (int j = i + 2; j < nodeCount; j++) {
                    int m = ant.getCycle().get(j);
                    if (isImportant[n][m] && (dist[n][m] < r || dist[n_nxt][n_nxt] < r)) {
                        int m_nxt = ant.getCycle().get((j + 1)%nodeCount);
                        if (isImportant[n_nxt][m_nxt]) {
                            double oldLength = dist[n][n_nxt] + dist[m][m_nxt];
                            double newLength = dist[n][m] + dist[n_nxt][m_nxt];
                            if (newLength < oldLength) {
                                ant.getDlb()[i]
                                        = ant.getDlb()[(i+1)%nodeCount]
                                        = ant.getDlb()[j%nodeCount]
                                        = ant.getDlb()[(j+1)%nodeCount]
                                        = false;

                                ant.setCycle(swap2Edges(ant.getCycle(), ant.getCycle().indexOf(n), ant.getCycle().indexOf(m)));
                                ant.setCycleLength(ant.getCycleLength() - (oldLength - newLength));
                                return;
                            }
                        }
                    }
                }
            }
            ant.getDlb()[i] = true;
        }
    }

    private void three_opt(Ant ant){
        for(int i = 0; i < nodeCount; i++){
            for (int j = i+2; j < nodeCount; j++) {
                for (int k = j+2; k < nodeCount-(i==0? 1 : 0); k++) {

                    int x = ant.getCycle().get(i);
                    int y = ant.getCycle().get(j);
                    int z = ant.getCycle().get(k);

                    int x2 = ant.getCycle().get((i+1)%nodeCount);
                    int y2 = ant.getCycle().get((j+1)%nodeCount);
                    int z2 = ant.getCycle().get((k+1)%nodeCount);

                    if(isImportant[x][y] && isImportant[y][z]){
                        for (int optCase = 7; optCase >= 1; optCase--) {
                            double oldLength = dist[x][x2] + dist[y][y2] + dist[z][z2];
                            double newLength = newEdgesLength(ant.getCycle(), i, j, k, optCase);
                            if(newLength < oldLength){
                                ant.setCycle(swap3Edges(ant.getCycle(), i, j, k, optCase));
                                ant.setCycleLength(ant.getCycleLength() - (oldLength - newLength));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private double newEdgesLength(ArrayList<Integer> cycle, int i1, int i2, int i3, int optCase){
        int n = cycle.get(i1);
        int m = cycle.get(i2);
        int k = cycle.get(i3);
        int n_next = cycle.get((i1 + 1)%nodeCount);
        int m_next = cycle.get((i2 + 1)%nodeCount);
        int k_next = cycle.get((i3 + 1)%nodeCount);

        if(optCase == 1){
            return dist[n][k] + dist[n_next][k_next] + dist[m][m_next];
        }
        else if(optCase == 2){
            return dist[n][n_next] + dist[m][k] + dist[m_next][k_next];
        }
        else if(optCase == 3){
            return dist[n][m] + dist[m_next][n_next] + dist[k][k_next];
        }
        else if(optCase == 4){
            return dist[n][m] + dist[m_next][k_next] + dist[k][n_next];
        }
        else if(optCase == 5){
            return dist[n][k] + dist[n_next][m_next] + dist[m][k_next];
        }
        else if(optCase == 6){
            return dist[n][m_next] + dist[m][k] + dist[k_next][n_next];
        }
        else if(optCase == 7){
            return dist[n][m_next] + dist[m][k_next] + dist[k][n_next];
        }
        else
            return 0;
    }

    private ArrayList<Integer> swap2Edges(ArrayList<Integer> cycle, int n1, int n2){
        ArrayList<Integer> newCycle = new ArrayList<>();
        for (int j = 0; j <= n1 ; j++) {
            newCycle.add(cycle.get(j));
        }
        for (int j = n2; j > n1; j--) {
            newCycle.add(cycle.get(j));
        }
        for (int j = n2 + 1; j < cycle.size(); j++) {
            newCycle.add(cycle.get(j));
        }
        return  newCycle;
    }

    private ArrayList<Integer> swap3Edges(ArrayList<Integer> cycle, int n1, int n2, int n3, int optCase){
        ArrayList<Integer> newCycle = new ArrayList<>();

        if(optCase == 1){
            newCycle = swap2Edges(cycle, n1, n3);
        }
        else if(optCase == 2){
            newCycle = swap2Edges(cycle, n2, n3);
        }
        else if(optCase == 3){
            newCycle = swap2Edges(cycle, n1, n2);
        }
        else if(optCase == 4){
            for (int i = 0; i <= n1 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2; i > n1 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3; i > n2 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size() ; i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 5){
            for (int i = 0; i <= n1 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3; i > n2 ; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n1 + 1; i <= n2 ; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size() ; i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 6){
            for (int i = 0; i <= n1; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2 + 1; i <= n3; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2; i > n1; i--) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size(); i++) {
                newCycle.add(cycle.get(i));
            }
        }
        else if(optCase == 7){
            for (int i = 0; i <= n1; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n2 + 1; i <= n3; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n1 + 1; i <= n2; i++) {
                newCycle.add(cycle.get(i));
            }
            for (int i = n3 + 1; i < cycle.size(); i++) {
                newCycle.add(cycle.get(i));
            }
        }
        return  newCycle;
    }

    public DoubleProperty bgLengthProperty() {
        return bgLength;
    }
}

