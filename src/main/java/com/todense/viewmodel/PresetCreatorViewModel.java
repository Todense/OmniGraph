package com.todense.viewmodel;

import com.todense.model.graph.Graph;
import com.todense.viewmodel.preset.Preset;
import com.todense.viewmodel.preset.PresetCreator;
import com.todense.viewmodel.scope.CanvasScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PresetCreatorViewModel implements ViewModel {
    
    private final ObjectProperty<Preset> presetProperty = new SimpleObjectProperty<>();

    private final ObjectProperty<Integer> param1ObjectProperty = new SimpleObjectProperty<>(10);
    private final IntegerProperty param1Property = IntegerProperty.integerProperty(param1ObjectProperty);

    private final ObjectProperty<Integer> param2ObjectProperty = new SimpleObjectProperty<>(10);
    private final IntegerProperty param2Property = IntegerProperty.integerProperty(param2ObjectProperty);

    @InjectScope
    CanvasScope canvasScope;

    @Inject
    NotificationCenter notificationCenter;

    public void initialize(){
        notificationCenter.subscribe("PRESET", (key, payload) -> createPreset());
    }

    private Graph create(Preset preset, Point2D size) {

        Graph presetGraph = null;

        switch (preset) {
            case GRID:
                presetGraph = PresetCreator.createGrid(getParam1(), getParam2(), size); break;
            case HEX:
                presetGraph = PresetCreator.createHexagonalGrid(getParam1(), size); break;
            case KING:
                presetGraph = PresetCreator.createKingsGraph(getParam1(), getParam2(), size); break;
            case MAZE:
                presetGraph = PresetCreator.createMaze(getParam1(), getParam2(), size); break;
            case CYCLE:
                presetGraph = PresetCreator.createCycle(getParam1(), 0.8 * size.getY()); break;
            case STAR:
                presetGraph = PresetCreator.createStar(getParam1(),0.4 * size.getY()); break;
            case COMPLETE_BIPARTITE:
                presetGraph = PresetCreator.createCompleteBipartite(getParam1(), getParam2(), size); break;
        }
        return presetGraph;
    }

    public void createPreset() {
        notificationCenter.publish(MainViewModel.THREAD_STARTED, "Creating preset...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(() -> {
            Graph presetGraph = create(presetProperty.get(), new Point2D(canvasScope.getCanvasWidth(), canvasScope.getCanvasHeight()));
            notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, presetGraph);
            notificationCenter.publish(MainViewModel.THREAD_FINISHED, presetProperty.get().toString() +" created");
        });
        executor.execute(thread);
    }

    public ObjectProperty<Preset> presetProperty() {
        return presetProperty;
    }

    public int getParam1() {
        return param1Property.get();
    }

    public int getParam2() {
        return param2Property.get();
    }

    public ObjectProperty<Integer> param1ObjectProperty() {
        return param1ObjectProperty;
    }

    public ObjectProperty<Integer> param2ObjectProperty() {
        return param2ObjectProperty;
    }

}
