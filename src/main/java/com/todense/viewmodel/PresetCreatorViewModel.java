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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PresetCreatorViewModel implements ViewModel {
    
    private final ObjectProperty<Preset> presetProperty = new SimpleObjectProperty<>();
    
    private final IntegerProperty gridWidthProperty = new SimpleIntegerProperty();
    private final IntegerProperty gridHeightProperty = new SimpleIntegerProperty();
    private final IntegerProperty cycleSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty hexGridSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty triangularGridSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty starSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty bipartiteFirstSetSizeProperty = new SimpleIntegerProperty();
    private final IntegerProperty bipartiteSecondSetSizeProperty = new SimpleIntegerProperty();

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
                presetGraph = PresetCreator.createGrid(getGridWidth(), getGridHeight(), size); break;
            case HEX:
                presetGraph = PresetCreator.createHexagonalGrid(getHexGridSize(), size); break;
            case TRIANGULAR:
                presetGraph = PresetCreator.createTriangularGrid(getTriangularGridSize(), size); break;
            case KING:
                presetGraph = PresetCreator.createKingsGraph(getGridWidth(), getGridHeight(), size); break;
            case MAZE:
                presetGraph = PresetCreator.createMaze(getGridWidth(), getGridHeight(), size); break;
            case CYCLE:
                presetGraph = PresetCreator.createCycle(getCycleSize(), 0.8 * size.getY()); break;
            case STAR:
                presetGraph = PresetCreator.createStar(getStarSize(),0.4 * size.getY()); break;
            case COMPLETE_BIPARTITE:
                presetGraph = PresetCreator.createCompleteBipartite(
                        getBipartiteFirstSetSize(), getBipartiteSecondSetSize(), size
                );break;
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

    public int getGridWidth() {
        return gridWidthProperty.get();
    }

    public IntegerProperty gridWidthProperty() {
        return gridWidthProperty;
    }

    public int getGridHeight() {
        return gridHeightProperty.get();
    }

    public IntegerProperty gridHeightProperty() {
        return gridHeightProperty;
    }

    public int getCycleSize() {
        return cycleSizeProperty.get();
    }

    public IntegerProperty cycleSizeProperty() {
        return cycleSizeProperty;
    }

    public int getStarSize() {
        return starSizeProperty.get();
    }

    public IntegerProperty starSizeProperty() {
        return starSizeProperty;
    }

    public int getBipartiteFirstSetSize() {
        return bipartiteFirstSetSizeProperty.get();
    }

    public IntegerProperty bipartiteFirstSetSizeProperty() {
        return bipartiteFirstSetSizeProperty;
    }

    public int getBipartiteSecondSetSize() {
        return bipartiteSecondSetSizeProperty.get();
    }

    public IntegerProperty bipartiteSecondSetSizeProperty() {
        return bipartiteSecondSetSizeProperty;
    }

    public int getHexGridSize() {
        return hexGridSizeProperty.get();
    }

    public IntegerProperty hexGridSizeProperty() {
        return hexGridSizeProperty;
    }

    public int getTriangularGridSize() {
        return triangularGridSizeProperty.get();
    }

    public IntegerProperty triangularGridSizeProperty() {
        return triangularGridSizeProperty;
    }
}
