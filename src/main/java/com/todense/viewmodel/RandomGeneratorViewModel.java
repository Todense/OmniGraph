package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.model.graph.Node;
import com.todense.viewmodel.random.Generator;
import com.todense.viewmodel.random.GeneratorModel;
import com.todense.viewmodel.random.RandomEdgeGenerator;
import com.todense.viewmodel.random.RandomGraphGenerator;
import com.todense.viewmodel.random.arrangement.NodeArrangement;
import com.todense.viewmodel.random.arrangement.generators.CircularPointGenerator;
import com.todense.viewmodel.random.arrangement.generators.RandomCirclePointGenerator;
import com.todense.viewmodel.random.arrangement.generators.RandomSquarePointGenerator;
import com.todense.viewmodel.random.generators.BarabasiAlbertGenerator;
import com.todense.viewmodel.random.generators.ErdosRenyiGenerator;
import com.todense.viewmodel.random.generators.GeometricGenerator;
import com.todense.viewmodel.scope.CanvasScope;
import com.todense.viewmodel.scope.GraphScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.*;
import javafx.geometry.Point2D;

import javax.inject.Inject;

public class RandomGeneratorViewModel implements ViewModel {

    private final ObjectProperty<GeneratorModel> generatorProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<NodeArrangement> nodeArrangementProperty = new SimpleObjectProperty<>();

    private final IntegerProperty nodeCountProperty = new SimpleIntegerProperty(50);
    private final DoubleProperty minNodeDistProperty = new SimpleDoubleProperty(0.1);
    private final DoubleProperty edgeProbabilityProperty = new SimpleDoubleProperty(0.1);
    private final DoubleProperty edgeThresholdProperty = new SimpleDoubleProperty(0.2);
    private final IntegerProperty barabasiInitialNodesProperty = new SimpleIntegerProperty(2);
    private final IntegerProperty barabasiConnectionsProperty = new SimpleIntegerProperty(2);


    private final BooleanProperty withMinDistProperty = new SimpleBooleanProperty(true);
    
    @Inject
    NotificationCenter notificationCenter;

    @InjectScope
    CanvasScope canvasScope;

    public void initialize(){
        notificationCenter.subscribe("RANDOM", (key, payload) -> generate());
    }

    private void generateGraph(){

        double height = canvasScope.getCanvasHeight() * 0.9;
        double minDist = withMinDistProperty.get() && nodeArrangementProperty.get() != NodeArrangement.CIRCULAR
                ? minNodeDistProperty.get() * height
                : 0d;

        Generator<Point2D> pointGenerator;
        RandomEdgeGenerator edgeGenerator;

        switch (nodeArrangementProperty.get()){
            case CIRCULAR:
                pointGenerator = new CircularPointGenerator(nodeCountProperty.get(), height/2);
                break;
            case RANDOM_SQUARE:
                pointGenerator = new RandomSquarePointGenerator(height);
                break;
            case RANDOM_CIRCLE:
                pointGenerator = new RandomCirclePointGenerator(height/2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeArrangementProperty.get());
        }

        switch (generatorProperty.get()){
            case GEOMETRIC:
                edgeGenerator = new GeometricGenerator(
                        edgeThresholdProperty.get() * height,
                        false
                );
                break;
            case GEOMETRIC_RANDOMIZED:
                edgeGenerator = new GeometricGenerator(
                        edgeProbabilityProperty.get() * height,
                        true
                );
                break;
            case ERDOS_RENYI:
                edgeGenerator = new ErdosRenyiGenerator(
                        edgeProbabilityProperty.get()
                );
                break;
            case BARABASI_ALBERT:
                edgeGenerator = new BarabasiAlbertGenerator(
                        barabasiInitialNodesProperty.get(),
                        barabasiConnectionsProperty.get());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + generatorProperty.get());
        }

        try{
            Graph randomGraph = RandomGraphGenerator.generateGraph(nodeCountProperty.get(), pointGenerator, edgeGenerator, minDist);
            notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, randomGraph);
            notificationCenter.publish(MainViewModel.THREAD_FINISHED, "Random graph generated");
        } catch (RuntimeException e){
            notificationCenter.publish(MainViewModel.THREAD_FINISHED, e.getMessage());
        }
    }

    public void generate(){
        notificationCenter.publish(MainViewModel.THREAD_STARTED, "Generating random graph...");
        Thread thread = new Thread(this::generateGraph);
        thread.start();
    }



    public ObjectProperty<GeneratorModel> generatorProperty() {
        return generatorProperty;
    }

    public ObjectProperty<NodeArrangement> nodeArrangementProperty() {
        return nodeArrangementProperty;
    }

    public BooleanProperty withMinDistProperty() {
        return withMinDistProperty;
    }

    public double getEdgeProbability() {
        return edgeProbabilityProperty.get();
    }

    public DoubleProperty edgeProbabilityProperty() {
        return edgeProbabilityProperty;
    }

    public double getEdgeThreshold() {
        return edgeThresholdProperty.get();
    }

    public DoubleProperty edgeThresholdProperty() {
        return edgeThresholdProperty;
    }

    public int getBarabasiInitialNodes() {
        return barabasiInitialNodesProperty.get();
    }

    public IntegerProperty barabasiInitialNodesProperty() {
        return barabasiInitialNodesProperty;
    }

    public int getBarabasiConnections() {
        return barabasiConnectionsProperty.get();
    }

    public IntegerProperty barabasiConnectionsProperty() {
        return barabasiConnectionsProperty;
    }

    public int getNodeCount() {
        return nodeCountProperty.get();
    }

    public IntegerProperty nodeCountProperty() {
        return nodeCountProperty;
    }

    public double getMinNodeDist() {
        return minNodeDistProperty.get();
    }

    public DoubleProperty minNodeDistProperty() {
        return minNodeDistProperty;
    }
}
