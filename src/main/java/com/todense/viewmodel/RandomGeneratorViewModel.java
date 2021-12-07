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

    private final ObjectProperty<Integer> nodeCountObjectProperty = new SimpleObjectProperty<>(50);
    private final IntegerProperty nodeCountProperty = IntegerProperty.integerProperty(nodeCountObjectProperty);

    private final ObjectProperty<Double> minNodeDistObjectProperty = new SimpleObjectProperty<>(0.1);
    private final DoubleProperty minNodeDistProperty = DoubleProperty.doubleProperty(minNodeDistObjectProperty);

    private final ObjectProperty<Double> doubleParameterObjectProperty = new SimpleObjectProperty<>(0.18);

    private final DoubleProperty doubleParameterProperty = DoubleProperty.doubleProperty(doubleParameterObjectProperty);

    private final ObjectProperty<Integer> intParameter1ObjectProperty = new SimpleObjectProperty<>(2);
    private final IntegerProperty intParameter1Property = IntegerProperty.integerProperty(intParameter1ObjectProperty);

    private final ObjectProperty<Integer> intParameter2ObjectProperty = new SimpleObjectProperty<>(2);
    private final IntegerProperty intParameter2Property = IntegerProperty.integerProperty(intParameter2ObjectProperty);

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
                        doubleParameterProperty.get() * height,
                        false
                );
                break;
            case GEOMETRIC_RANDOMIZED:
                edgeGenerator = new GeometricGenerator(
                        doubleParameterProperty.get() * height,
                        true
                );
                break;
            case ERDOS_RENYI:
                edgeGenerator = new ErdosRenyiGenerator(
                        doubleParameterProperty.get()
                );
                break;
            case BARABASI_ALBERT:
                edgeGenerator = new BarabasiAlbertGenerator(
                        intParameter1Property.get(),
                        intParameter2Property.get());
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

    public ObjectProperty<Integer> nodeCountObjectProperty() {
        return nodeCountObjectProperty;
    }

    public ObjectProperty<Double> minNodeDistObjectProperty() {
        return minNodeDistObjectProperty;
    }

    public ObjectProperty<Double> doubleParameterObjectProperty() {
        return doubleParameterObjectProperty;
    }

    public ObjectProperty<Integer> intParameter1ObjectProperty() {
        return intParameter1ObjectProperty;
    }

    public ObjectProperty<Integer> intParameter2ObjectProperty() {
        return intParameter2ObjectProperty;
    }

    public BooleanProperty withMinDistProperty() {
        return withMinDistProperty;
    }
}
