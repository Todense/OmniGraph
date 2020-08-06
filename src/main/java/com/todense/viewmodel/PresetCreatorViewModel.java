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
    
    private ObjectProperty<Preset> presetProperty = new SimpleObjectProperty<>();

    private ObjectProperty<Integer> param1ObjectProperty = new SimpleObjectProperty<>(10);
    private IntegerProperty param1Property = IntegerProperty.integerProperty(param1ObjectProperty);

    private ObjectProperty<Integer> param2ObjectProperty = new SimpleObjectProperty<>(10);
    private IntegerProperty param2Property = IntegerProperty.integerProperty(param2ObjectProperty);

    @InjectScope
    CanvasScope canvasScope;

    @Inject
    NotificationCenter notificationCenter;

    private void create(Preset preset, Point2D center) {

        Graph presetGraph = null;

        switch (preset) {
            case GRID:
                presetGraph = PresetCreator.createGrid(getParam1(), getParam2(), center); break;
            case KING:
                presetGraph = PresetCreator.createKingsGraph(getParam1(), getParam2(), center); break;
            case MAZE:
                presetGraph = PresetCreator.createMaze(getParam1(), getParam2(), center); break;
            case CYCLE:
                presetGraph = PresetCreator.createCycle(getParam1(), 0.8 * center.getY(), center); break;
            case STAR:
                presetGraph = PresetCreator.createStar(getParam1(),0.8 * center.getY(), center); break;
            case COMPLETE_BIPARTITE:
                presetGraph = PresetCreator.createCompleteBipartite(getParam1(), getParam2(), center); break;
        }

        notificationCenter.publish(GraphViewModel.NEW_GRAPH_REQUEST, presetGraph);
    }

    public void createPreset() {
        notificationCenter.publish(MainViewModel.threadStarted, "Creating preset...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Thread thread = new Thread(() -> {
            create(presetProperty.get(), new Point2D(canvasScope.getCanvasWidth()/2, canvasScope.getCanvasHeight()/2));
            notificationCenter.publish(MainViewModel.threadFinished, presetProperty.get().toString() +" created");
        });
        executor.execute(thread);
        //thread.start();
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
